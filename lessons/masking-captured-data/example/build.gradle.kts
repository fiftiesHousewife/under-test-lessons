plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Masking captured data: a day of real data masked so the tests that run on it still give the right answers.
//   ./gradlew :lessons:masking-captured-data:test -Pcode=today   today's masking: joins break, and a field is missed
//   ./gradlew :lessons:masking-captured-data:test                keyed substitutes: the reports agree, nothing is left
dependencies {
    implementation(libs.datafaker)
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

// `./gradlew :lessons:masking-captured-data:test -Pcode=answer` runs today's masking with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's masking is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
