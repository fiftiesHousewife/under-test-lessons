import groovy.json.JsonSlurper

plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Running old code unplugged: an old Spring Boot quote service whose settings name production's database and services.
// Its tests run under the airgap agent, which refuses every call to another machine, as an unplugged machine would,
// and reports each one. The test profile then gives each refused address a stand-in, until the report is clean.
//   ./gradlew :lessons:running-old-code-unplugged:test -Pcode=today   the service's own settings: every test is refused
//   ./gradlew :lessons:running-old-code-unplugged:test                with the test profile's stand-ins: a clean report
dependencies {
    implementation(platform(libs.spring.boot.dependencies)) // Spring Boot's BOM sets the starters' versions
    implementation(libs.spring.boot.starter.jdbc) // the shop's database, through JdbcTemplate
    implementation(libs.spring.boot.starter.restclient) // the courier's delivery rates and the payment gateway
    runtimeOnly(libs.postgresql) // production's database is PostgreSQL
    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.h2) // the database's stand-in: H2, in memory
    // The stand-in for the courier's rates. Spring Boot's BOM would lift plain WireMock's Jetty 11 to Jetty 12, where
    // it can't start; the standalone jar keeps its own Jetty inside.
    testImplementation(libs.wiremock.standalone)
}

// The airgap agent: its jar, named on the test JVM's command line, and its JUnit listener, which tells it which test
// is running, so the report names it.
val airgapAgent by configurations.creating
dependencies {
    airgapAgent(libs.airgap.agent)
    testImplementation(libs.airgap.junit)
}

// Every test JVM starts with the agent. It writes its report to build/airgap/test/calls.jsonl, and names the line of
// the service's own code behind each call: the code under this project's group. A JVM can only start an agent named
// on its command line, so the agent can't be an ordinary dependency.
tasks.test {
    val agent = files(airgapAgent)
    val report = layout.buildDirectory.file("airgap/test/calls.jsonl")
    val ownCode = "${project.group}."
    inputs.files(agent) // a new agent runs the tests again
    doFirst { report.get().asFile.delete() } // each run's report starts empty
    jvmArgumentProviders.add(CommandLineArgumentProvider {
        listOf("-javaagent:${agent.singleFile}=report=${report.get().asFile};project=$ownCode")
    })
}

// Logging goes through Log4j 2, as in every lesson, so Spring Boot's default, Logback, is left out.
configurations.configureEach {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

// Today the tests run with the service's own settings only. The fix runs them with the test profile, whose
// application-test.yml gives each address the agent refused a stand-in. `-Pcode=answer` runs the exercise's answer
// (answer/*AnswerTest) on its own, with the test profile; -Pcode=today leaves it out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    if (code != "today") {
        systemProperty("spring.profiles.active", "test")
    }
    when (code) {
        "today" -> filter { excludeTestsMatching("*AnswerTest") }
        "answer" -> filter { includeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// After each run, a short summary of the agent's report (build/airgap/test/calls.jsonl): each address it refused,
// once, with the tests that tried it and the line of the service's code that called it, and, found by this task, the
// setting in application.yml that holds the address.
val airgapSummary = tasks.register("airgapSummary") {
    description = "Summarises the airgap agent's report of the last test run."
    val report = layout.buildDirectory.file("airgap/test/calls.jsonl")
    val settingsFile = layout.projectDirectory.file("src/main/resources/application.yml")
    doLast {
        val file = report.get().asFile
        val calls = if (file.isFile) file.readLines().filter { it.isNotBlank() } else emptyList()
        val refused = calls.map { JsonSlurper().parseText(it) as Map<*, *> }.filter { it["outcome"] == "refused" }
        if (refused.isEmpty()) {
            logger.quiet("The airgap agent refused nothing: no test tried to reach another machine.")
            return@doLast
        }
        val settings = settingsFile.asFile.readLines()
        fun indent(line: String) = line.indexOfFirst { it != ' ' }
        fun key(line: String) = line.trim().substringBefore(':')
        // The dotted name of the YAML setting on the line that names the host, from the keys it sits under.
        fun settingFor(host: String): String {
            val at = settings.indexOfFirst { host in it }
            if (at < 0) {
                return "not in application.yml"
            }
            val keys = mutableListOf(key(settings[at]))
            var depth = indent(settings[at])
            for (line in settings.subList(0, at).reversed().filter { it.isNotBlank() && !it.trim().startsWith("#") }) {
                if (indent(line) < depth) {
                    keys.add(0, key(line))
                    depth = indent(line)
                }
            }
            return "${keys.joinToString(".")} (application.yml:${at + 1})"
        }
        // io.example.shop.ProductPrices.pricePence(ProductPrices.java:19) -> ProductPrices.pricePence(ProductPrices.java:19)
        fun shortCaller(frame: String) = frame.substringBefore('(').split('.').takeLast(2).joinToString(".") +
            "(" + frame.substringAfter('(')
        fun shortTest(test: String) = test.split('.').takeLast(2).joinToString(".")
        // Each machine once, whatever the kinds of call: a host lookup, then a connection to one of its ports.
        val byHost = refused.groupBy { it["target"].toString().substringBefore(':') }
        logger.quiet("The airgap agent refused ${refused.size} calls to ${byHost.size} machines:")
        byHost.forEach { (host, each) ->
            logger.quiet("  $host")
            logger.quiet("    tests:   " + each.mapNotNull { it["test"]?.toString() }.distinct().map(::shortTest)
                .ifEmpty { listOf("(on a thread no test owns)") }.joinToString(", "))
            logger.quiet("    code:    " + each.map { it["caller"].toString() }.filter { it.contains('(') && !it.startsWith("(") }
                .map(::shortCaller).distinct().ifEmpty { listOf("(not found)") }.joinToString(", "))
            logger.quiet("    setting: " + settingFor(host))
        }
    }
}
tasks.test {
    finalizedBy(airgapSummary)
}
tasks.named("testFailures") {
    mustRunAfter(airgapSummary) // the summary prints before the build's own "2 of 3 tests failed"
}
