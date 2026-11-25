plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Temporary folders: JUnit's @TempDir gives each test its own empty folder, and removes it afterwards.
// It comes with the build conventions' JUnit, so there is nothing to add.
//   ./gradlew :lessons:junit-temporary-folders:test -Pcode=today   today's export writes no file when an order is cancelled
//   ./gradlew :lessons:junit-temporary-folders:test                the fixed export always writes the file
// `-Pcode=answer` runs today's export with the exercise's answer (the answer package), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", code)
    if (code == "today") {
        filter { excludeTestsMatching("*.answer.*") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's export is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
