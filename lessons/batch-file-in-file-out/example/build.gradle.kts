plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Batch file-in, file-out: run the whole Spring Batch job on a known input file, and compare its output file's records.
//   ./gradlew :lessons:batch-file-in-file-out:test -Pcode=today   today's job: only the whole-job tests fail
//   ./gradlew :lessons:batch-file-in-file-out:test                the fixed job: every record matches
dependencies {
    implementation(libs.spring.batch.core)
    testImplementation(libs.spring.batch.test)
    testImplementation(libs.datafaker)
    testCompileOnly(libs.spotbugs.annotations)
}

// `./gradlew :lessons:batch-file-in-file-out:test -Pcode=answer` runs today's job with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's job is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
