import java.net.URI

plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// The tools that make lesson videos and read narration aloud: VHS, ttyd, Python, the Kokoro voice and ffmpeg. Only the
// machine that makes the videos runs them, never a company (docs/decisions/0025 and 0026); the build works without
// this folder. They use the inside video tools (lessons/_shared/video-tools). The main tasks:
//   renderLessonVideo:     makes a lesson's video (-Plesson=<id>; -PslidesOnly=true for just its slides).
//   buildLessonDeckVoice:  reads each deck slide's narration aloud (-Plesson=<id>, or every lesson).
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
}

// The offline voice, set up once per machine under build/voice. An organisation can use its own Python
// (-PlessonVoicePython) and a mirror of the model files (-PlessonVoiceModelsUrl); pip uses the machine's own pip settings.
val voiceDir = rootDir.resolve("build/voice")
val voicePython = providers.gradleProperty("lessonVoicePython").orElse("python3.13")
val voiceModelsUrl = providers.gradleProperty("lessonVoiceModelsUrl")
    .orElse("https://github.com/thewh1teagle/kokoro-onnx/releases/download/model-files-v1.0")
val voiceModels = listOf("kokoro-v1.0.onnx", "voices-v1.0.bin")

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

// The voice's packages, at exact versions, so the voice sounds the same on every machine. Changing a version here
// installs it again on the next run.
val voicePackages = listOf("kokoro-onnx==0.6.1", "soundfile==0.14.0")
val installVoicePackages = tasks.register<Exec>("installVoicePackages") {
    val dir = voiceDir
    val packages = voicePackages
    dependsOn(createVoiceEnvironment)
    onlyIf {
        val installed = dir.resolve("venv/.packages-installed")
        !installed.isFile || installed.readText().trim() != packages.joinToString(" ")
    }
    commandLine(listOf(dir.resolve("venv/bin/pip").absolutePath, "install", "-q") + packages)
    doLast { dir.resolve("venv/.packages-installed").writeText(packages.joinToString(" ") + "\n") }
}

val setUpLessonVoice = tasks.register("setUpLessonVoice") {
    group = "documentation"
    description = "Sets up the offline Kokoro voice under build/voice: Python environment, packages and model files."
    val dir = voiceDir
    val base = voiceModelsUrl
    val models = voiceModels
    dependsOn(installVoicePackages)
    onlyIf { models.any { !dir.resolve(it).isFile } }
    doLast {
        models.filterNot { dir.resolve(it).isFile }.forEach { name ->
            URI.create("${base.get()}/$name").toURL().openStream().use { input ->
                dir.resolve("$name.part").outputStream().use { input.copyTo(it) }
            }
            dir.resolve("$name.part").renameTo(dir.resolve(name))
        }
    }
}

val buildLessonVoice = tasks.register<JavaExec>("buildLessonVoice") {
    dependsOn(setUpLessonVoice)
    group = "documentation"
    description = "Reads each scene's narration aloud and builds the narration track (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    workingDir = rootDir
    environment("KOKORO_MODELS", "$repoRoot/build/voice")
    systemProperty("video.voiceReadsLists", voiceReadsLists.get())
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
