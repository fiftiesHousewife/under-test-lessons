plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Tags: fast tests first, slow tests apart. JUnit's @Tag comes with the build conventions' JUnit, so there is nothing
// to add; the build gives each kind of test its own task.
//   ./gradlew :lessons:junit-tags:test -Pcode=today   today's build: no tags, so the quick task runs the slow tests too
//   ./gradlew :lessons:junit-tags:test                the quick task leaves out the tests tagged slow
//   ./gradlew :lessons:junit-tags:slowTest            only the tests tagged slow; check runs them, so CI does
val todaysBuild = providers.gradleProperty("code").getOrElse("fixed") == "today"

// The quick task, for every change.
tasks.test {
    useJUnitPlatform {
        if (!todaysBuild) {
            excludeTags("slow")
        }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching builds never shows a stale result
}

// The slow task, for CI: the same test classes, only those tagged slow.
val slowTest = tasks.register<Test>("slowTest") {
    description = "Runs only the tests tagged slow."
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform {
        includeTags("slow")
    }
    shouldRunAfter(tasks.test)
    outputs.upToDateWhen { false }
    testLogging {
        quiet {
            events("passed", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = false
        }
    }
    // A one-line summary, as the quick task prints.
    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ suite, result ->
        if (suite.parent == null) {
            logger.quiet("${result.testCount} tests tagged slow ran, ${result.failedTestCount} failed.")
        }
    }))
}
tasks.check {
    dependsOn(slowTest)
}
