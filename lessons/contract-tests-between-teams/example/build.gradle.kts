plugins {
    id("io.github.fiftieshousewife.lesson")
}

// Contract tests between teams: the order screen and the stock service check one agreement, a pact, with Pact JVM.
//   ./gradlew :lessons:contract-tests-between-teams:test -Pcode=today   today's stock service: the pact check names the renamed field
//   ./gradlew :lessons:contract-tests-between-teams:test                the fixed stock service: every test passes
dependencies {
    implementation(libs.jackson3.databind)
    testImplementation(libs.pact.consumer.junit5)
    testImplementation(libs.pact.provider.junit5)
}

// `./gradlew :lessons:contract-tests-between-teams:test -Pcode=answer` runs today's stock service with the exercise's
// answer (answer/*AnswerTest), which -Pcode=today leaves out.
val code = providers.gradleProperty("code").getOrElse("fixed")
tasks.test {
    systemProperty("code", if (code == "answer") "today" else code)
    if (code == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
    // The order screen's pact test writes its pact here, never over the published copy, and replaces it on every run.
    systemProperty("pact.rootDir", "build/pacts")
    systemProperty("pact.writer.overwrite", "true")
    // Pact JVM sends anonymous usage figures over the internet unless told not to; tests here stay offline.
    systemProperty("pact_do_not_track", "true")
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Stands in for the order screen's build publishing its pact to the company's package repository, as version 1.0.0.
// In a real build this is a Maven publication of the pact file; the stock service's build then depends on it by version.
tasks.register<Copy>("publishPact") {
    description = "Copies the pact the order screen's tests wrote into the folder that stands in for the package repository."
    dependsOn(tasks.test)
    from(layout.buildDirectory.dir("pacts"))
    into(layout.projectDirectory.dir("package-repository/order-screen-pacts/1.0.0"))
}

// Today's stock service is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
