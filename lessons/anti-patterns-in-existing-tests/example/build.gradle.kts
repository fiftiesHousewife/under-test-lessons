plugins {
    alias(libs.plugins.java.conventions)
}

// Anti-patterns in existing tests: an old shop's tests, read by checks the build runs for what makes them
// untrustworthy. Each finding is listed with its test, file and line, and written down in a register with a decision;
// the register then refuses any new one. No old test is changed or deleted.
//   ./gradlew :lessons:anti-patterns-in-existing-tests:test -Pcode=today   no register yet: every finding is listed, none recorded
//   ./gradlew :lessons:anti-patterns-in-existing-tests:test                the register written down: each finding has its decision
dependencies {
    testImplementation(libs.archunit) // reads the old tests' compiled classes
    testImplementation(libs.mockito.core) // for the names of Mockito's annotations and methods, which the checks look for
}

// The old shop, as it came to us: its code and its tests, in old-shop/. Its tests are compiled but never run here, and
// never changed: the checks read their compiled classes. It is the lesson's subject, not this repository's code, so
// this repository's own quality checks don't apply to it.
val oldShop by sourceSets.creating {
    java.setSrcDirs(listOf("old-shop/src/main/java", "old-shop/src/test/java"))
}
dependencies {
    "oldShopImplementation"(platform(libs.junit.bom))
    "oldShopImplementation"(libs.junit.jupiter)
    "oldShopImplementation"(libs.assertj.core)
    "oldShopImplementation"(libs.mockito.core)
    testRuntimeOnly(oldShop.output) // the checks import the old shop's classes from the test class path
}
listOf("checkstyleOldShop", "pmdOldShop", "spotbugsOldShop", "forbiddenApisOldShop").forEach {
    tasks.named(it) { enabled = false }
}

// The register: the committed file of anti-patterns found and triaged. Today there is none yet. `-Pcode=answer` runs
// the exercise's answer (answer/*AnswerTest) on its own; -Pcode=today leaves it out.
val code = providers.gradleProperty("code").getOrElse("fixed")
val findingsDir = layout.buildDirectory.dir("anti-patterns")
tasks.test {
    val register = layout.projectDirectory.file("old-shop/anti-patterns.txt")
    inputs.file(register)
    systemProperty("antiPatterns.register", if (code == "today") "" else register.asFile.path)
    systemProperty("antiPatterns.report", findingsDir.get().file("findings.txt").asFile.path)
    systemProperty("antiPatterns.answerReport", findingsDir.get().file("answer.txt").asFile.path)
    val dir = findingsDir
    doFirst { dir.get().asFile.run { deleteRecursively(); mkdirs() } } // each run's report starts empty
    when (code) {
        "today" -> filter { excludeTestsMatching("*AnswerTest") }
        "answer" -> filter { includeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// After each run, the findings as a table: printed even at Gradle's quiet logging level, before the build's own
// summary of the tests.
val antiPatternRegister = tasks.register("antiPatternRegister") {
    description = "Prints the anti-patterns the last test run found, each with its decision from the register."
    val report = findingsDir.map { it.file(if (code == "answer") "answer.txt" else "findings.txt") }
    doLast {
        val file = report.get().asFile
        if (file.isFile) {
            file.readLines().forEach { logger.quiet(it) }
        }
    }
}
tasks.test {
    finalizedBy(antiPatternRegister)
}
tasks.named("testSummary") {
    mustRunAfter(antiPatternRegister)
}
