import java.net.URI
import java.security.MessageDigest

plugins {
    id("io.github.fiftieshousewife.lesson")
}

// The tools that make lesson videos and read narration aloud: VHS, ttyd, Python, the Kokoro voice and ffmpeg. Only the
// machine that makes the videos runs them, never a company (docs/decisions/0025 and 0026); the build works without
// this folder. They use the inside video tools (lessons/_shared/video-tools). The main tasks:
//   renderLessonVideo:     makes a lesson's video (-Plesson=<id>; -PslidesOnly=true for just its slides).
//   buildLessonDeckVoice:  reads each deck slide's narration aloud (-Plesson=<id>, or every lesson).
//   recordLessonDeckClips: records each live-run scene on its own, for the deck (-Plesson=<id>).
//   checkVideoInRecording: checks every video step with the real tools in half a minute.
//   testVideoHelpers:      tests the in-terminal helpers against a stand-in for VHS (needs bash).

dependencies {
    implementation(project(":lessons:video-tools"))
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

val repoRoot = rootDir.absolutePath
val lesson = providers.gradleProperty("lesson")
// The voice: any command that turns a text file into a WAV file. Default: the offline Kokoro voice (see speak-kokoro.py).
// Words are split at spaces; put one in double quotes to keep its spaces: -PlessonVoiceCommand='"/My Voice/say" --fast'.
// A command that also reads a list (--list <file>, one text file and WAV a line, as speak-kokoro.py does) loads its
// voice once a lesson rather than once a clip: say so with -PlessonVoiceReadsLists=true. The default voice does.
val customVoice = providers.gradleProperty("lessonVoiceCommand")
val voiceCommand = customVoice.map { command ->
    Regex(""""([^"]*)"|(\S+)""").findAll(command).map { it.groupValues[1].ifEmpty { it.groupValues[2] } }.toList()
}.orElse(listOf("$repoRoot/build/voice/venv/bin/python", "$repoRoot/production/video-render/speak-kokoro.py"))
val voiceReadsLists = providers.gradleProperty("lessonVoiceReadsLists").orElse(customVoice.map { "false" })
    .orElse("true")

val testVideoHelpers = tasks.register<Exec>("testVideoHelpers") {
    group = "verification"
    description = "Tests the in-terminal video helpers in lessons/_shared/callout.sh."
    workingDir = rootDir
    commandLine("bash", "production/video-render/test-video-helpers.sh")
}

val recordLessonVideo = tasks.register<JavaExec>("recordLessonVideo") {
    group = "documentation"
    description = "Records a lesson's silent video and GIF with VHS, showing brief progress (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    val root = repoRoot
    val lessonId = lesson
    argumentProviders.add(CommandLineArgumentProvider { listOf("record", root, lessonId.getOrElse("")) })
    // Every script is checked before VHS starts: a stale wait or a missing output fails in under a minute, not ten.
    dependsOn(":lessons:video-tools:checkVideoScripts")
}

// The offline voice, set up once per machine under build/voice. An organisation can use its own Python
// (-PlessonVoicePython) and a mirror of the model files (-PlessonVoiceModelsUrl); pip uses the machine's own pip settings.
val voiceDir = rootDir.resolve("build/voice")
val voicePython = providers.gradleProperty("lessonVoicePython").orElse("python3.13")
val voiceModelsUrl = providers.gradleProperty("lessonVoiceModelsUrl")
    .orElse("https://github.com/thewh1teagle/kokoro-onnx/releases/download/model-files-v1.0")
// The model files and the SHA-256 of the copies every lesson's voice was made with; the release publishes none of its
// own. A file that doesn't match is refused, so the voice is never made from a different or damaged model.
val voiceModels = mapOf(
    "kokoro-v1.0.onnx" to "7d5df8ecf7d4b1878015a32686053fd0eebe2bc377234608764cc0ef3636a6c5",
    "voices-v1.0.bin" to "bca610b8308e8d99f32e6fe4197e7ec01679264efed0cac9140fe9c29f1fbf7d")

val createVoiceEnvironment = tasks.register<Exec>("createVoiceEnvironment") {
    val dir = voiceDir
    val asked = voicePython
    onlyIf { !dir.resolve("venv/bin/python").isFile }
    doFirst {
        val onPath = { program: String ->
            File(program).isAbsolute && File(program).canExecute() ||
                System.getenv("PATH").orEmpty().split(File.pathSeparator).any { File(it, program).canExecute() }
        }
        val python = asked.get().takeIf(onPath) ?: "python3".also {
            logger.warn("${asked.get()} isn't installed, so the voice is set up with python3. If its packages won't" +
                " install, install Python 3.13 or name another with -PlessonVoicePython=<program>.")
        }
        commandLine(python, "-m", "venv", dir.resolve("venv").absolutePath)
    }
}

// The voice's packages: every one, kokoro-onnx's own included, at an exact version and with the SHA-256 of each file,
// in voice-requirements.txt, so the voice is made from the same code on every machine. Only kokoro-onnx is used, never
// the kokoro package. Changing the file installs it again on the next run.
val voiceRequirements = layout.projectDirectory.file("voice-requirements.txt").asFile
val installVoicePackages = tasks.register<Exec>("installVoicePackages") {
    val dir = voiceDir
    val requirements = voiceRequirements
    dependsOn(createVoiceEnvironment)
    inputs.file(requirements)
    onlyIf {
        val installed = dir.resolve("venv/.packages-installed")
        !installed.isFile || installed.readText() != requirements.readText()
    }
    commandLine(dir.resolve("venv/bin/pip").absolutePath, "install", "-q", "--require-hashes", "--no-deps", "-r",
        requirements.absolutePath)
    doLast { requirements.copyTo(dir.resolve("venv/.packages-installed"), overwrite = true) }
}

val setUpLessonVoice = tasks.register("setUpLessonVoice") {
    group = "documentation"
    description = "Sets up the offline Kokoro voice under build/voice: Python environment, packages and model files."
    val dir = voiceDir
    val base = voiceModelsUrl
    val models = voiceModels
    dependsOn(installVoicePackages)
    doLast {
        val sha256 = { file: File ->
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { input ->
                val buffer = ByteArray(1 shl 20)
                generateSequence { input.read(buffer).takeIf { it >= 0 } }.forEach { digest.update(buffer, 0, it) }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        }
        models.keys.filterNot { dir.resolve(it).isFile }.forEach { name ->
            URI.create("${base.get()}/$name").toURL().openStream().use { input ->
                dir.resolve("$name.part").outputStream().use { input.copyTo(it) }
            }
            dir.resolve("$name.part").renameTo(dir.resolve(name))
        }
        models.forEach { (name, expected) ->
            val found = sha256(dir.resolve(name))
            if (found != expected) {
                throw GradleException("build/voice/$name has SHA-256 $found, not the $expected the lessons' voice was" +
                    " made with: delete it to download it again, or check the mirror (-PlessonVoiceModelsUrl).")
            }
        }
    }
}

// The voice engine in one word, recorded beside each clip the voice makes (voices.txt), so a clip made with another
// program, voice, speed, model or package set is read again: kokoro-onnx-<version>/<voice>/<speed>/<model checksums>
// /<package list checksum>, or command-<checksum> for another voice command. The models' checksums are the recorded
// ones, which setUpLessonVoice holds the files to.
fun shortSha256(text: String) = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
    .joinToString("") { "%02x".format(it) }.take(8)
val voiceEngine = customVoice.map { "command-" + shortSha256(it) }.orElse(provider {
    val requirements = voiceRequirements.readText()
    val version = Regex("""kokoro-onnx==(\S+)""").find(requirements)?.groupValues?.get(1) ?: "unknown"
    val voice = System.getenv("KOKORO_VOICE") ?: "bf_emma"
    val speed = System.getenv("KOKORO_SPEED") ?: "1.0"
    "kokoro-onnx-$version/$voice/$speed/models-" + voiceModels.values.joinToString("-") { it.take(8) } +
        "/packages-" + shortSha256(requirements)
})

val buildLessonVoice = tasks.register<JavaExec>("buildLessonVoice") {
    dependsOn(setUpLessonVoice)
    group = "documentation"
    description = "Reads each scene's narration aloud and builds the narration track (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    workingDir = rootDir
    environment("KOKORO_MODELS", "$repoRoot/build/voice")
    systemProperty("video.voiceReadsLists", voiceReadsLists.get())
    systemProperty("video.voiceEngine", voiceEngine.get())
    val root = repoRoot
    val lessonId = lesson
    val voice = voiceCommand
    argumentProviders.add(CommandLineArgumentProvider { listOf("voice", root, lessonId.getOrElse("")) + voice.get() })
}

tasks.register<JavaExec>("assembleLessonVideo") {
    group = "documentation"
    description = "Combines the silent video, narration and captions into the finished MP4 (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    val root = repoRoot
    val lessonId = lesson
    argumentProviders.add(CommandLineArgumentProvider { listOf("assemble", root, lessonId.getOrElse("")) })
    dependsOn(":lessons:video-tools:buildLessonCaptions", buildLessonVoice)
}

// The deck's live-run slides play their scene, recorded on its own, in place of the kept output.
tasks.register<JavaExec>("recordLessonDeckClips") {
    group = "documentation"
    description = "Records each live-run scene of a lesson on its own, for its deck to play (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    val root = repoRoot
    val lessonId = lesson
    argumentProviders.add(CommandLineArgumentProvider { listOf("clips", root, lessonId.getOrElse("")) })
    dependsOn(":lessons:video-tools:checkVideoScripts")
}

// Later steps use the recorded video's real length, so they run after recording when both are asked for.
project(":lessons:video-tools").tasks.named("buildLessonCaptions") { mustRunAfter(recordLessonVideo) }
buildLessonVoice.configure { mustRunAfter(recordLessonVideo) }

apply(from = rootDir.resolve("lessons/_shared/video-settings.gradle.kts"))

// Half a minute that checks every step a lesson video goes through, with the real tools, before any lesson is recorded:
// a few seconds of VHS (code rolling in, a zoom, a callout sliding in), a slide built up by the browser, the two joined
// and a caption burned in. Leaves build/video-check/video-check.mp4 to watch.
tasks.register<JavaExec>("checkVideoInRecording") {
    group = "verification"
    description = "Checks every video step with the real tools in half a minute, leaving a short video to watch."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.VideoCheck"
    workingDir = rootDir
    val root = repoRoot
    argumentProviders.add(CommandLineArgumentProvider { listOf(root) })
}

// A deck's voice: each slide's narration read aloud with the lesson voice into <id>.deck-voice/ beside the deck, which
// is rewritten to play it (Listen, or V). Clips are named by what they say, so only changed slides are read again.
// Commit the clips with the deck; they are published with it. -Plesson=<id>, or every lesson.
tasks.register<JavaExec>("buildLessonDeckVoice") {
    dependsOn(setUpLessonVoice)
    group = "documentation"
    description = "Reads each deck slide's narration aloud, so the deck can be heard without the video (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    workingDir = rootDir
    environment("KOKORO_MODELS", "$repoRoot/build/voice")
    systemProperty("video.voiceReadsLists", voiceReadsLists.get())
    systemProperty("video.voiceEngine", voiceEngine.get())
    val root = repoRoot
    val lessonId = lesson
    val voice = voiceCommand
    argumentProviders.add(CommandLineArgumentProvider {
        listOf("deckvoice", root, lessonId.getOrElse("all")) + voice.get()
    })
}

// -PslidesOnly=true draws only the slides, in about a minute, instead of making the whole video.
val slidesOnly = providers.gradleProperty("slidesOnly").map { it.toBoolean() }.orElse(false)
tasks.register("renderLessonVideo") {
    group = "documentation"
    description = "Makes a lesson's whole video: records it, builds captions and voice, and assembles the MP4 (-Plesson=<id>)."
    if (slidesOnly.get()) {
        dependsOn(":lessons:video-tools:drawLessonSlides")
    } else {
        dependsOn(recordLessonVideo, "assembleLessonVideo")
    }
}

tasks.check {
    dependsOn(testVideoHelpers)
}
