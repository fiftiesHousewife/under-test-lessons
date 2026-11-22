plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// When an in-memory database is enough: each test gets its own H2 database, held in memory, with the real schema,
// instead of one shared test database that every test leaves rows in. Nothing to install, and no container.
//   ./gradlew :lessons:in-memory-databases:test -Pcode=today   today's numbering: the shared database hides invoice 0
//   ./gradlew :lessons:in-memory-databases:test                the fixed numbering: the first invoice is number 1
dependencies {
    compileOnly(libs.spotbugs.annotations)
    testCompileOnly(libs.spotbugs.annotations)
    testImplementation(libs.h2) // the in-memory database: one library on the test classpath, nothing to install
}

tasks.test {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// Today's numbering is the "before", run with -Pcode=today; coverage is measured on the rest.
tasks.jacocoTestCoverageVerification {
    val fixedCode = sourceSets.main.get().output.classesDirs.asFileTree.matching { exclude("**/today/**") }
    classDirectories.setFrom(fixedCode)
}
