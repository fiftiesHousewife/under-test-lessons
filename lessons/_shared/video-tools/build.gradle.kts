plugins {
    alias(libs.plugins.java.conventions)
}

// The deck tools that run anywhere the lessons do, inside a company included: the checks of lesson scene files,
// slides and narration, slide pictures and decks. The tool that reads narration aloud is in production/deck-voice,
// which only the machine that voices the decks runs. The main tasks:
//   checkLessonDecks:   catches mistakes in what decks are built from: scene and narration counts, code panels that
//                       would show the wrong file or lines, live runs without kept output or that don't fit one
//                       screen, slides without sources or repository links, and slides without a voice clip.
//   buildLessonDecks:   rebuilds every deck from its scene file, slides, narration and kept outputs.
//   buildLessonSlides:  rebuilds every deck and slide image from the current stylesheets and pictures.

dependencies {
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

val repoRoot = rootDir.absolutePath
val lesson = providers.gradleProperty("lesson")

val checkLessonDecks = tasks.register<JavaExec>("checkLessonDecks") {
    group = "verification"
    description = "Fails on lesson scene files, slides, narration and deck voices with mistakes a deck would show."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.DeckChecks"
    args(repoRoot)
    // Clips no slide uses are listed; -PremoveUnused=true removes them, a person's recordings too.
    systemProperty("video.removeUnused", providers.gradleProperty("removeUnused").orElse("false").get())
}

apply(from = rootDir.resolve("lessons/_shared/deck-settings.gradle.kts"))

val drawLessonSlides = tasks.register<JavaExec>("drawLessonSlides") {
    group = "documentation"
    description = "Draws a lesson's slides as images, for a quick look (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.LessonDecks"
    val root = repoRoot
    val lessonId = lesson
    argumentProviders.add(CommandLineArgumentProvider { listOf("slides", root, lessonId.getOrElse("all")) })
}

// Slide decks: one HTML file per lesson, committed next to its scene file (-Plesson=<id>, or every lesson).
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

// A printable script for a person to voice a lesson's deck, where no speech engine runs (docs/decisions/0027): the
// file to save each clip as, the slide's title, its words with the pauses as directions, and pronunciation hints.
// -Plesson=<id>; -Pmissing=true lists only the slides with no clip yet. Written to build/recording-scripts/<id>.html.
tasks.register<JavaExec>("recordingScript") {
    group = "documentation"
    description = "Writes a printable script for a person to record a lesson's voice clips (-Plesson=<id>)."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.video.RecordingScript"
    val root = repoRoot
    val lessonId = lesson
    val missing = providers.gradleProperty("missing").map { it.toBoolean() }.orElse(false)
    argumentProviders.add(CommandLineArgumentProvider {
        listOf(root, lessonId.getOrElse("")) + (if (missing.get()) listOf("missing") else emptyList())
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
    dependsOn(checkLessonDecks)
}

// The deck tools are one package: its classes share package-private methods, so it holds more than the usual 20.
underTest {
    maxClassesPerPackage = 34
}

// The deck's own script and styles are tested here (DeckListenTest drives deck.js), so a change to them reruns the tests.
tasks.test {
    inputs.dir(rootDir.resolve("lessons/_shared/slides")).withPropertyName("sharedSlides")
}
