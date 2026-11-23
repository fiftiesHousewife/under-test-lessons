plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Coverage of changed code: an old pricing module with no tests can't pass a whole-module coverage gate, so the gates
// apply only to the classes this change touched, listed in changed-classes.txt (in CI, written from git diff).
//   ./gradlew :lessons:coverage-of-changed-code:pitest -Pcode=today   the change's weak test: planted bugs survive
//   ./gradlew :lessons:coverage-of-changed-code:pitest                the stronger test: every planted bug caught
val changedClasses = file("changed-classes.txt").readLines().map { it.trim() }
    .filter { it.isNotEmpty() && !it.startsWith("#") }
val code = providers.gradleProperty("code").getOrElse("fixed")
val unusedTests = if (code == "today") "*StrongTest" else "*WeakTest"

// PIT plants bugs only in the changed classes; every test may run against them.
pitest {
    targetClasses = changedClasses.map { "*.$it" }
    targetTests = listOf("${project.group}.*")
    excludedTestClasses = listOf(unusedTests)
}

// After PIT's summary, the gate: every planted bug in the changed classes must be caught.
val changedCodeGate = tasks.register("changedCodeGate") {
    description = "Fails unless the tests caught every planted bug in the changed classes."
    val report = layout.buildDirectory.file("reports/pitest/mutations.xml")
    doLast {
        val results = Regex("status='([A-Z_]+)'").findAll(report.get().asFile.readText()).map { it.groupValues[1] }.toList()
        val missed = results.count { it != "KILLED" && it != "TIMED_OUT" }
        if (missed > 0) {
            throw GradleException("$missed of ${results.size} planted bugs in the changed classes survived")
        }
        logger.quiet("Gate passed: every planted bug in the changed classes was caught.")
    }
}
tasks.named("pitest") {
    outputs.upToDateWhen { false } // a lesson always reruns PIT, so switching tests never shows a stale result
    finalizedBy(changedCodeGate)
}
changedCodeGate { mustRunAfter("mutationSummary") }

tasks.test {
    filter { excludeTestsMatching(unusedTests) }
    outputs.upToDateWhen { false }
}

// The coverage gate counts only the changed classes, and asks for every line of them to run.
tasks.jacocoTestCoverageVerification {
    val changed = sourceSets.main.get().output.classesDirs.asFileTree
        .matching { changedClasses.forEach { include("**/$it.class", "**/$it$*.class") } }
    classDirectories.setFrom(changed)
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "1.0".toBigDecimal()
            }
        }
    }
}

// The exercise's answer (answer/*AnswerTest): `./gradlew :lessons:coverage-of-changed-code:pitest -Pcode=answer` runs
// PIT with the change's weak test and the answer, one more test. PIT's other runs leave the answer out, and so does
// -Pcode=today's test run; `check` runs it with the fixed code's tests.
pitest {
    excludedTestClasses = if (code == "answer") listOf("*StrongTest") else listOf(unusedTests, "*AnswerTest")
}
if (code == "today") {
    tasks.test { filter { excludeTestsMatching("*AnswerTest") } }
}
