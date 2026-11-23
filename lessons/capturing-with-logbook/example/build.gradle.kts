plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Capturing what an old service receives and sends: Logbook records each exchange, its filters remove secrets before
// anything is written, and the capture is kept as an approved file.
//   ./gradlew :lessons:capturing-with-logbook:test -Pcode=today   today's capture: the token and the email are written
//   ./gradlew :lessons:capturing-with-logbook:test                the filtered capture: it matches its approved file
dependencies {
    implementation(libs.logbook.core)
    implementation(libs.logbook.json)
    implementation(libs.logbook.servlet)
    implementation(libs.logbook.httpclient5)
    implementation(libs.jetty.ee11.servlet)
    implementation(libs.httpclient5)
    implementation(libs.jackson3.databind)
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
    testImplementation(libs.approvaltests)
}

val testSources = layout.projectDirectory.dir("src/test/java").asFile

val approvalSummary = tasks.register("approvalSummary") {
    description = "Shows, line by line, how any received output differs from its approved file."
    val dir = testSources
    doLast {
        /** The lines removed (-) and added (+) between two texts, using their longest common run of lines. */
        fun differences(before: List<String>, after: List<String>): List<String> {
            val common = Array(before.size + 1) { IntArray(after.size + 1) }
            for (i in before.indices.reversed()) for (j in after.indices.reversed()) {
                common[i][j] = if (before[i] == after[j]) common[i + 1][j + 1] + 1 else maxOf(common[i + 1][j], common[i][j + 1])
            }
            val out = mutableListOf<String>()
            var i = 0
            var j = 0
            while (i < before.size || j < after.size) {
                when {
                    i < before.size && j < after.size && before[i] == after[j] -> { i++; j++ }
                    j < after.size && (i == before.size || common[i][j + 1] > common[i + 1][j]) -> out += "+ ${after[j++]}"
                    else -> out += "- ${before[i++]}"
                }
            }
            return out
        }

        val received = dir.walkTopDown().filter { it.name.endsWith(".received.txt") }.toList()
        if (received.isEmpty()) {
            logger.quiet("Approvals: every output matches its approved file.")
            return@doLast
        }
        received.forEach { file ->
            val approved = File(file.path.removeSuffix(".received.txt") + ".approved.txt")
            logger.quiet("Approvals: the output differs from its approved file (- approved, + received):")
            differences(if (approved.isFile) approved.readLines() else emptyList(), file.readLines())
                .forEach { logger.quiet("  $it") }
        }
    }
}

// `./gradlew :lessons:capturing-with-logbook:test -Pcode=answer` runs today's capture with the exercise's answer
// (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
    finalizedBy(approvalSummary) // shows, line by line, how a capture differs from its approved file
    val sources = testSources
    doFirst { // a capture left from an earlier run, such as the answer's, must not show in this run's summary
        sources.walkTopDown().filter { it.name.endsWith(".received.txt") }.forEach { it.delete() }
    }
}

// Today's capture is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
