plugins {
    alias(libs.plugins.java.conventions)
}

// Spring tests in slices: a Spring Boot 4 delivery service tested one layer at a time (web, data, JSON), with one
// test of the whole application kept for the wiring.
//   ./gradlew :lessons:spring-tests-in-slices:test -Pcode=today   today's service: the JSON slice names the date written wrongly
//   ./gradlew :lessons:spring-tests-in-slices:test                the fixed service: every test passes
dependencies {
    implementation(platform(libs.spring.boot.dependencies)) // Spring Boot's BOM sets the starters' versions
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.restclient) // the client for the courier's tracking service
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test) // the web slice: @WebMvcTest
    testImplementation(libs.spring.boot.starter.data.jpa.test) // the data slice: @DataJpaTest
    // The courier, stood in for by WireMock in the tests of the whole application. Spring Boot's BOM would lift plain
    // WireMock's Jetty 11 to Jetty 12, where it can't start; the standalone jar keeps its own Jetty inside.
    testImplementation(libs.wiremock.standalone)
    testRuntimeOnly(libs.h2) // the in-memory database every test uses
    compileOnly(libs.spotbugs.annotations) // to note a reviewed SpotBugs finding in the code
    testCompileOnly(libs.spotbugs.annotations)
}

// Logging goes through Log4j 2, as in every lesson, so Spring Boot's default, Logback, is left out.
configurations.configureEach {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

// `-Pcode=answer` runs today's service with the exercise's answer (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code) // the application knows only today and fixed
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's answer is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
