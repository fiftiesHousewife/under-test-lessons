plugins {
    base
}

// The voice review: a page for a person to hear what the lesson voice says and change it by clicking, with no
// phonetic symbols to read or type (README.md). Production only, like deck-voice: it runs the Kokoro voice that
// :production:deck-voice:setUpLessonVoice installs. The tasks:
//   reviewVoice:          runs the review at http://localhost:8765/ until stopped (-PvoiceReviewPort=<port>).
//   buildVoiceReviewSet:  builds the review set of phrases, words and punctuation from review-set.txt, and voices it.
//   testVoiceReview:      tests the narration handling, without the voice (needs python3 only); part of check.

val repoRoot = rootDir.absolutePath
val voicePython = "$repoRoot/build/voice/venv/bin/python"
val port = providers.gradleProperty("voiceReviewPort").orElse("8765")

val reviewVoice = tasks.register<Exec>("reviewVoice") {
    group = "application"
    description = "Runs the voice review at http://localhost:8765/ until stopped (Ctrl+C)."
    dependsOn(":production:deck-voice:setUpLessonVoice")
    workingDir = rootDir
    val chosen = port
    commandLine(voicePython, "production/voice-review/server.py")
    argumentProviders.add(CommandLineArgumentProvider { listOf(chosen.get()) })
}

val buildVoiceReviewSet = tasks.register<Exec>("buildVoiceReviewSet") {
    group = "application"
    description = "Builds the voice review set from review-set.txt and the decks' narration, and voices new lines."
    dependsOn(":production:deck-voice:setUpLessonVoice")
    workingDir = rootDir
    commandLine(voicePython, "production/voice-review/review_set.py")
}

val testVoiceReview = tasks.register<Exec>("testVoiceReview") {
    group = "verification"
    description = "Tests the voice review's narration handling, with a made-up lesson and no voice."
    workingDir = projectDir
    inputs.files(fileTree(projectDir) { include("*.py") })
    outputs.upToDateWhen { false }
    commandLine("python3", "-m", "unittest", "discover", "-s", ".", "-p", "test_*.py")
}

tasks.check {
    dependsOn(testVoiceReview)
}
