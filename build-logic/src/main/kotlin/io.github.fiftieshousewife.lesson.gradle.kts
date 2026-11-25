// Every project in this repository applies this plugin. It applies the shared code-quality standards, the published
// io.github.fiftieshousewife.java-conventions plugin, and adds only what the lessons need on top: test results that
// read well in a recording, the container tests' own suite, and Mockito's agent. The standards themselves, and their
// settings (underTest.*), are the plugin's: see its README.

plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

val libs = the<VersionCatalogsExtension>().named("libs")
fun library(alias: String) = libs.findLibrary(alias).get()

// ---------- Tests that read well in a recording ----------
// Failing tests end the build with a short message and a report path relative to the project, instead of Gradle's long
// message with the full path, which shows a person's home folder in recordings and logs. The build still fails.
fun summaryName(testTask: String) = if (testTask == "test") "testFailures" else "${testTask}Failures"

/**
 * Registers the short summary for one test task: "All N tests passed.", or "N of M tests failed. Report: ...". With
 * skipsFail, a skipped test fails the build too: container tests skip themselves on a machine without a container
 * runtime, but CI must run them.
 */
fun registerFailureSummary(testTask: String, skipsFail: Provider<Boolean>) = tasks.register(summaryName(testTask)) {
    description = "Fails the build, briefly, if any test in $testTask failed."
    val results = layout.buildDirectory.dir("test-results/$testTask")
    val failOnSkips = skipsFail
    doLast {
        val counts = Regex("<testsuite [^>]*?tests=\"(\\d+)\"[^>]*?skipped=\"(\\d+)\"[^>]*?failures=\"(\\d+)\"[^>]*?errors=\"(\\d+)\"")
        val suites = results.get().asFile.walkTopDown().filter { it.name.endsWith(".xml") }
            .mapNotNull { counts.find(it.readText())?.groupValues?.drop(1)?.map(String::toInt) }.toList()
        val total = suites.sumOf { it[0] }
        val skipped = suites.sumOf { it[1] }
        val failed = suites.sumOf { it[2] + it[3] }
        if (failed > 0) {
            throw GradleException("$failed of $total tests failed. Report: build/reports/tests/$testTask/index.html")
        }
        if (skipped > 0 && failOnSkips.get()) {
            throw GradleException("$skipped of $total tests in $testTask were skipped, and CI must run them all: " +
                "give CI a container runtime")
        }
        val ran = total - skipped
        logger.quiet(
            when {
                skipped == 0 -> "All $total tests passed."
                ran == 0 -> "No tests ran; $skipped skipped."
                else -> "All $ran tests that ran passed; $skipped skipped."
            },
        )
    }
}
val testFailures = registerFailureSummary("test", provider { false })
// In CI a skipped container test fails the build, except where the job says containers can't run there
// (-PcontainerTestsMaySkip=true, as on Windows runners, whose Docker runs only Windows containers).
registerFailureSummary("integrationTest", providers.gradleProperty("containerTestsMaySkip").map { false }
    .orElse(providers.environmentVariable("CI").map { true }).orElse(false))

// The container tests' task, integrationTest, reports the same way, where a project has one.
tasks.withType<Test>().matching { it.name == "integrationTest" }.configureEach {
    useJUnitPlatform()
    ignoreFailures = true
    testLogging {
        quiet {
            events("passed", "failed", "skipped")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = false
            showCauses = false
        }
    }
    finalizedBy("integrationTestFailures")
}

// Mockito's inline mock maker needs its agent. JDK 21 and later warn when a library attaches an agent to a running
// JVM, and a later JDK will refuse, so every test JVM is given Mockito's agent when it starts instead.
val mockitoAgent = configurations.create("mockitoAgent") { isTransitive = false }
dependencies {
    mockitoAgent(library("mockito-core"))
}
tasks.withType<Test>().configureEach {
    val agent: FileCollection = mockitoAgent
    inputs.files(agent).withPropertyName("mockitoAgent").withNormalizer(ClasspathNormalizer::class.java)
    // With an agent on the class path the JVM can only share its own classes, and says so on every run; tests don't
    // need class sharing, so it is off, and their output stays clean.
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:" + agent.singleFile.absolutePath, "-Xshare:off")
    })
}

tasks.test {
    useJUnitPlatform()
    ignoreFailures = true
    // Each test's result shows even at Gradle's quiet logging level, which recordings use; failures without stack traces.
    testLogging {
        quiet {
            events("passed", "failed")
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStackTraces = false
            showCauses = false
        }
    }
    finalizedBy(testFailures)
}

// The plugin's mutation testing (pitest) always reruns in a lesson, so switching between test suites with -Pcode never
// shows a stale result.
tasks.matching { it.name == "pitest" }.configureEach {
    outputs.upToDateWhen { false }
}
