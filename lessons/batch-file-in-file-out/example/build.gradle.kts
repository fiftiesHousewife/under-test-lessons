plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Batch file-in, file-out: run the whole job on a known input file, and compare its output file line by line.
//   ./gradlew :lessons:batch-file-in-file-out:test -Pcode=today   today's job: only the file comparison fails
//   ./gradlew :lessons:batch-file-in-file-out:test                the fixed job: its file matches, line for line
tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's job is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
