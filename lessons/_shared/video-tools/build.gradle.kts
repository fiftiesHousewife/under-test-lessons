plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// The video tools that run anywhere the lessons do, inside a company included: the checks of lesson video scripts,
// captions from narration, slide pictures and decks. The tools that make videos and read narration aloud are in
// production/video-render, which only the machine that makes the videos runs. The main tasks:
//   checkVideoScripts:  catches script mistakes before recording: waits that hang, scene and narration counts, code
//                       panels that would show the wrong file or lines, scenes too short for their narration, slides
//                       without sources or repository links, live runs that don't fit one screen.
//   buildLessonSlides:  rebuilds every deck and slide image from the current stylesheets and pictures.

dependencies {
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

val repoRoot = rootDir.absolutePath
val lesson = providers.gradleProperty("lesson")

val checkVideoScripts = tasks.register<JavaExec>("checkVideoScripts") {
    group = "verification"
    description = "Fails on lesson video scripts (.tape) with mistakes that would make a recording hang."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.VideoChecks"
    args(repoRoot)
    // Clips no slide uses are listed; -PremoveUnused=true removes them, a person's recordings too.
    systemProperty("video.removeUnused", providers.gradleProperty("removeUnused").orElse("false").get())
}

tasks.register<JavaExec>("buildLessonCaptions") {
    group = "documentation"
    description = "Writes WebVTT and SRT captions for each lesson video from its narration (-Plesson=<id> for one)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.Captions"
    args(listOfNotNull(repoRoot, providers.gradleProperty("lesson").orNull))
}

apply(from = rootDir.resolve("lessons/_shared/video-settings.gradle.kts"))

val drawLessonSlides = tasks.register<JavaExec>("drawLessonSlides") {
    group = "documentation"
    description = "Draws a lesson's slides as images, for a quick look (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonDecks"
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
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonDecks"
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

tasks.check {
    dependsOn(checkVideoScripts)
}
