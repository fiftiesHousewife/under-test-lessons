plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Old command-line programs: environment variables, standard input and output with System Stubs; System.exit passed in.
//   ./gradlew :lessons:old-command-line-programs:test -Pcode=today   today's program: its System.exit stops the test run
//   ./gradlew :lessons:old-command-line-programs:test                the program that is given its exit: every test passes
dependencies {
    testImplementation(libs.system.stubs.jupiter)
    testCompileOnly(libs.spotbugs.annotations) // System Stubs' classes carry SpotBugs annotations
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
    // System Stubs replaces System.getenv with Byte Buddy's agent. Given to the test JVM when it starts, so the JVM
    // never has to attach it to itself, which newer JDKs warn about and sandboxes refuse.
    val byteBuddyAgent = configurations.testRuntimeClasspath.get().filter { it.name.startsWith("byte-buddy-agent") }
    jvmArgumentProviders.add(CommandLineArgumentProvider { listOf("-javaagent:" + byteBuddyAgent.singleFile.absolutePath) })
}

// Today's program ends the JVM, so no test can finish running it: that is the lesson. Coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}

// `./gradlew :lessons:old-command-line-programs:test -Pcode=answer` runs the exercise's answer (answer/*AnswerTest)
// alone, against today's program: today's System.exit would end the run in the strong tests. -Pcode=today leaves it out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.named<Test>("test") {
    filter {
        when (code) {
            "today" -> excludeTestsMatching("*AnswerTest")
            "answer" -> includeTestsMatching("*AnswerTest")
        }
    }
}
