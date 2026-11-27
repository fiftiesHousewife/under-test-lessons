plugins {
    alias(libs.plugins.java.conventions)
}

// Fuzzing: Jazzer hands an old label reader inputs nobody wrote, and finds the one that crashes it.
//   ./gradlew :lessons:fuzzing:fuzz -Pcode=today   search for new inputs: Jazzer finds today's reader's crash
//   ./gradlew :lessons:fuzzing:test -Pcode=today   replay the kept inputs: the crash it found fails the build
//   ./gradlew :lessons:fuzzing:test                the fixed reader: every kept input is read or refused
dependencies {
    testImplementation(libs.jazzer.junit)
}

val code = providers.gradleProperty("code").getOrElse("fixed")

// Jazzer watches which lines each input reaches through an agent, which it puts into the running test process with
// the copy of Byte Buddy it carries. Attaching to a running process needs an attach socket, which locked-down machines
// and sandboxes refuse, so the tests start with the agent already in place instead: a jar holding nothing but a
// manifest that names that Byte Buddy's start-up class, and points at Jazzer's jar. Byte Buddy then finds its agent
// installed, and attaches nothing.
val jazzerJar = configurations.testRuntimeClasspath.map { classpath ->
    classpath.filter { it.name.matches(Regex("jazzer-\\d.*\\.jar")) }.singleFile
}
val jazzerAgentJar = tasks.register<Jar>("jazzerAgentJar") {
    archiveFileName = "jazzer-agent.jar"
    destinationDirectory = layout.buildDirectory.dir("jazzer")
    val jar = jazzerJar
    manifest {
        attributes(
            "Premain-Class" to "com.code_intelligence.jazzer.third_party.net.bytebuddy.agent.Installer",
            "Class-Path" to jar.map { it.toURI().toString() },
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true",
            "Can-Set-Native-Method-Prefix" to "true",
        )
    }
}

// Each run starts from the kept inputs alone: the folder of inputs a search found interesting is cleared first.
val searchCorpus = layout.projectDirectory.dir(".cifuzz-corpus")

// The test runs and the search share their set-up: the switch, the agent, and a fresh start.
fun Test.fuzzing() {
    dependsOn(jazzerAgentJar)
    val agent = jazzerAgentJar.flatMap { it.archiveFile }
    jvmArgumentProviders.add(CommandLineArgumentProvider { listOf("-javaagent:" + agent.get().asFile.absolutePath) })
    // Jazzer joins the boot class path, uses sun.misc.Unsafe and loads a native library; these keep the JVM's warnings
    // about all three off screen.
    jvmArgs("-Xshare:off", "--sun-misc-unsafe-memory-access=allow", "--enable-native-access=ALL-UNNAMED")
    systemProperty("code", code)
    val corpus = searchCorpus
    doFirst { corpus.asFile.deleteRecursively() }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching readers never shows a stale result
}

// An ordinary test run replays the inputs kept beside each fuzz test, as regression tests. `-Pcode=answer` runs
// today's reader with the exercise's answer (answer/*AnswerTest), which -Pcode=today leaves out.
tasks.test {
    fuzzing()
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
}

// The fuzz task searches: JAZZER_FUZZ makes Jazzer generate inputs for the lesson's fuzz test, for at most its
// maxDuration, and save any that fail beside the test, named crash-<checksum>, to be kept and replayed by every
// test run after.
tasks.register<Test>("fuzz") {
    group = "verification"
    description = "Fuzzes the label reader with Jazzer for up to 30 seconds (-Pcode=today for today's reader)."
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform()
    fuzzing()
    environment("JAZZER_FUZZ", "1")
    filter { includeTestsMatching("*.LabelReaderFuzzTest") }
}

// Today's reader is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
