plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Cleaning captured data: a captured day cleaned so the tests that run on it still give the right answers.
//   ./gradlew :lessons:cleaning-captured-data:test -Pcode=today   today's cleaning: joins break, and a field is missed
//   ./gradlew :lessons:cleaning-captured-data:test                keyed substitutes: the reports agree, nothing is left
dependencies {
    implementation(libs.datafaker)
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

// `./gradlew :lessons:cleaning-captured-data:test -Pcode=answer` runs today's cleaning with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's cleaning is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
