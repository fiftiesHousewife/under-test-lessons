plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// A characterisation test pins down what untested code does today, so it can be changed safely:
//   ./gradlew :lessons:characterisation-tests:test                        the legacy printer: matches its approved file
//   ./gradlew :lessons:characterisation-tests:test -Pstatement=refactored a tidier rewrite: shows what changed
dependencies {
    testImplementation(libs.approvaltests)
}

val statement = providers.gradleProperty("statement").getOrElse("legacy")
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

tasks.test {
    systemProperty("statement.version", statement)
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching printers never shows a stale result
    finalizedBy(approvalSummary)
    // ApprovalTests' failure message lists full file paths; the approval summary explains the difference instead.
    testLogging {
        quiet {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
        }
    }
}
