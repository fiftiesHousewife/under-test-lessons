import java.net.URI
import java.security.MessageDigest

plugins {
    alias(libs.plugins.java.conventions)
}

// The deck's voice: reads each slide's narration aloud with Python and the offline Kokoro voice, and compresses each
// clip with ffmpeg. Only the machine that voices the decks runs it, never a company (docs/decisions/0026 and 0041); the
// build works without this folder. It uses the inside deck tools (lessons/_shared/video-tools). The main tasks:
//   setUpLessonVoice:      sets up the voice under build/voice, once per machine.
//   buildLessonDeckVoice:  reads each deck slide's narration aloud (-Plesson=<id>, or every lesson).

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
}.orElse(listOf("$repoRoot/build/voice/venv/bin/python", "$repoRoot/production/deck-voice/speak-kokoro.py"))
val voiceReadsLists = providers.gradleProperty("lessonVoiceReadsLists").orElse(customVoice.map { "false" })
    .orElse("true")

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
// /<package list checksum>/<the script that turns text into sounds' checksum>, or command-<checksum> for another voice
// command. The models' checksums are the recorded
// ones, which setUpLessonVoice holds the files to.
fun shortSha256(text: String) = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
    .joinToString("") { "%02x".format(it) }.take(8)
val speakScript = file("speak-kokoro.py")
val voiceEngine = customVoice.map { "command-" + shortSha256(it) }.orElse(provider {
    val requirements = voiceRequirements.readText()
    val version = Regex("""kokoro-onnx==(\S+)""").find(requirements)?.groupValues?.get(1) ?: "unknown"
    val voice = System.getenv("KOKORO_VOICE") ?: "bf_emma"
    val speed = System.getenv("KOKORO_SPEED") ?: "1.0"
    "kokoro-onnx-$version/$voice/$speed/models-" + voiceModels.values.joinToString("-") { it.take(8) } +
        "/packages-" + shortSha256(requirements) + "/script-" + shortSha256(speakScript.readText())
})

apply(from = rootDir.resolve("lessons/_shared/deck-settings.gradle.kts"))

// A deck's voice: each slide's narration read aloud with the lesson voice into <id>.deck-voice/ beside the deck, which
// is rewritten to play it (Listen, or V). Clips are named by what they say, so only changed slides are read again.
// Commit the clips with the deck; they are published with it. -Plesson=<id>, or every lesson.
tasks.register<JavaExec>("buildLessonDeckVoice") {
    dependsOn(setUpLessonVoice)
    group = "documentation"
    description = "Reads each deck slide's narration aloud, so the deck can be heard (-Plesson=<id>, or every lesson)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonDeckVoice"
    workingDir = rootDir
    environment("KOKORO_MODELS", "$repoRoot/build/voice")
    systemProperty("video.voiceReadsLists", voiceReadsLists.get())
    systemProperty("video.voiceEngine", voiceEngine.get())
    val root = repoRoot
    val lessonId = lesson
    val voice = voiceCommand
    argumentProviders.add(CommandLineArgumentProvider { listOf(root, lessonId.getOrElse("all")) + voice.get() })
}

// The deck's voice is one package with the inside deck tools, sharing their package-private methods.
underTest {
    maxClassesPerPackage = 22
}
