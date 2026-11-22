import java.net.URI

plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Tools for lesson videos: checks, captions and (to come) rendering. Kept inside lessons/ so it moves with the lessons when they get their own repo.
//   checkVideoScripts: catches video-script mistakes that otherwise only show up as a recording that hangs.
//   testVideoHelpers:  tests the shell helpers and render script against a stand-in for VHS (needs bash).

dependencies {
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

val repoRoot = rootDir.absolutePath
val lesson = providers.gradleProperty("lesson")
// The voice: any command that turns a text file into a WAV file. Default: the offline Kokoro voice (see speak-kokoro.py).
val voiceCommand = providers.gradleProperty("lessonVoiceCommand").map { it.split(" ") }.orElse(
    listOf("$repoRoot/build/voice/venv/bin/python", "$repoRoot/lessons/_shared/video-tools/speak-kokoro.py"))

val checkVideoScripts = tasks.register<JavaExec>("checkVideoScripts") {
    group = "verification"
    description = "Fails on lesson video scripts (.tape) with mistakes that would make a recording hang."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.VideoChecks"
    args(repoRoot)
}

val testVideoHelpers = tasks.register<Exec>("testVideoHelpers") {
    group = "verification"
    description = "Tests the in-terminal video helpers in lessons/_shared/callout.sh."
    workingDir = rootDir
    commandLine("bash", "lessons/_shared/video-tools/test-video-helpers.sh")
}

tasks.register<JavaExec>("buildLessonCaptions") {
    group = "documentation"
    description = "Writes WebVTT and SRT captions for each lesson video from its narration (-Plesson=<id> for one)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.Captions"
    args(listOfNotNull(repoRoot, providers.gradleProperty("lesson").orNull))
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
    onlyIf { !dir.resolve("venv/bin/python").isFile }
    commandLine(voicePython.get(), "-m", "venv", dir.resolve("venv").absolutePath)
}

val installVoicePackages = tasks.register<Exec>("installVoicePackages") {
    val dir = voiceDir
    dependsOn(createVoiceEnvironment)
    onlyIf { !dir.resolve("venv/.packages-installed").isFile }
    commandLine(dir.resolve("venv/bin/pip").absolutePath, "install", "-q", "kokoro-onnx", "soundfile")
    doLast { dir.resolve("venv/.packages-installed").writeText("kokoro-onnx soundfile\n") }
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
    dependsOn("buildLessonCaptions", buildLessonVoice)
}

// Later steps use the recorded video's real length, so they run after recording when both are asked for.
tasks.named("buildLessonCaptions") { mustRunAfter(recordLessonVideo) }
buildLessonVoice.configure { mustRunAfter(recordLessonVideo) }

// Slides are styled by lessons/_shared/slides/slides.css, then any user stylesheet: -PlessonSlideCss=<path>.
// A relative path is taken from the repository root, where the commands are run.
val repositoryDir = rootDir
val slideCss = providers.gradleProperty("lessonSlideCss").filter { it.isNotBlank() }
    .map { File(it).let { css -> if (css.isAbsolute) css else repositoryDir.resolve(it) }.absolutePath }.orElse("")
// Slides are screenshotted by a headless browser: -PlessonBrowser=<program>, or Google Chrome where it usually is.
val macChrome = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
val slideBrowser = providers.gradleProperty("lessonBrowser")
    .orElse(if (File(macChrome).canExecute()) macChrome else "google-chrome")
// Your own pictures, by name, used before the shared ones: -PlessonPictures=<folder> (for example the ink set,
// lessons/_shared/slides/pictures/ink). Set it, and lessonSlideCss, in gradle.properties to use them on every run.
val pictureFolder = providers.gradleProperty("lessonPictures").filter { it.isNotBlank() }
    .map { File(it).let { dir -> if (dir.isAbsolute) dir else repositoryDir.resolve(it) }.absolutePath }.orElse("")
// Where the lessons and the toolkit live, for the links on slides: lessonsRepoUrl and toolkitRepoUrl in
// gradle.properties. Unset, slides describe the repositories without an address.
val lessonsRepoUrl = providers.gradleProperty("lessonsRepoUrl").orElse("")
val toolkitRepoUrl = providers.gradleProperty("toolkitRepoUrl").orElse("")
tasks.withType<JavaExec>().configureEach {
    systemProperty("video.lessonsRepo", lessonsRepoUrl.get())
    systemProperty("video.toolkitRepo", toolkitRepoUrl.get())
    systemProperty("video.pictures", pictureFolder.get())
    systemProperty("video.slideCss", slideCss.get())
    systemProperty("video.browser", slideBrowser.get())
}

val drawLessonSlides = tasks.register<JavaExec>("drawLessonSlides") {
    group = "documentation"
    description = "Draws a lesson's slides as images, for a quick look (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    val root = repoRoot
    val lessonId = lesson
    argumentProviders.add(CommandLineArgumentProvider { listOf("slides", root, lessonId.getOrElse("all")) })
}

// Slide decks: one HTML file per lesson, committed next to its video script (-Plesson=<id>, or every lesson).
// -PlessonDeckPdf=true also prints each deck to a PDF with the browser, one slide per page.
tasks.register<JavaExec>("buildLessonDecks") {
    group = "documentation"
    description = "Writes each lesson's slide deck as HTML (and PDF with -PlessonDeckPdf=true)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonVideo"
    val root = repoRoot
    val lessonId = lesson
    val pdf = providers.gradleProperty("lessonDeckPdf").map { it.toBoolean() }.orElse(false)
    argumentProviders.add(CommandLineArgumentProvider {
        listOf("deck", root, lessonId.getOrElse("all")) + (if (pdf.get()) listOf("pdf") else emptyList())
    })
}

// One command to pick up revised stylesheets and pictures: rebuilds every lesson's deck and slide images (or one
// lesson's, with -Plesson=<id>). It always reruns, so edits are never missed.
tasks.register("buildLessonSlides") {
    group = "documentation"
    description = "Rebuilds slide decks and slide images from the current stylesheets and pictures."
    dependsOn("buildLessonDecks", drawLessonSlides)
}

// -PslidesOnly=true draws only the slides, in about a minute, instead of making the whole video.
val slidesOnly = providers.gradleProperty("slidesOnly").map { it.toBoolean() }.orElse(false)
tasks.register("renderLessonVideo") {
    group = "documentation"
    description = "Makes a lesson's whole video: records it, builds captions and voice, and assembles the MP4 (-Plesson=<id>)."
    if (slidesOnly.get()) {
        dependsOn(drawLessonSlides)
    } else {
        dependsOn(recordLessonVideo, "assembleLessonVideo")
    }
}

tasks.check {
    dependsOn(checkVideoScripts, testVideoHelpers)
}
