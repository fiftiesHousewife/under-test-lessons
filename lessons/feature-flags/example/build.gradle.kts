plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Feature flags: old and new code side by side behind a flag, and tests that run both, until the old code goes.
//   ./gradlew :lessons:feature-flags:test -Pcode=today   today's new shipping rules: only the both-ways test fails
//   ./gradlew :lessons:feature-flags:test                the fixed new rules: both ways give the same answers
dependencies {
    implementation(libs.openfeature.sdk)
    // OpenFeature's classes carry SpotBugs annotations; the compiler wants to see them.
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's new rules are the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
