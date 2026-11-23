plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Real databases in tests: the same repository tested on an in-memory database, and on real PostgreSQL in a container.
// Container tests live in their own suite, integrationTest, and skip themselves where there is no container runtime.
//   ./gradlew :lessons:real-databases-in-tests:test integrationTest -Pcode=today   today's query: only PostgreSQL fails
//   ./gradlew :lessons:real-databases-in-tests:test integrationTest                the fixed query: both databases agree
testing {
    suites {
        named<JvmTestSuite>("test") {
            dependencies {
                implementation(libs.h2)
            }
        }
        register<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(project())
                implementation(platform(libs.junit.bom))
                implementation(libs.junit.jupiter)
                implementation(libs.assertj.core)
                implementation(libs.testcontainers.postgresql)
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.postgresql)
                runtimeOnly(libs.junit.platform.launcher)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    systemProperty("code", providers.gradleProperty("code").getOrElse("fixed"))
    // The PostgreSQL image container tests start: a company's own, from its registry, with -PpostgresImage=<name>.
    systemProperty("postgres.image", providers.gradleProperty("postgresImage").getOrElse("postgres:17-alpine"))
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

// `-Pcode=answer` runs today's query with the exercise's answer (answer/*AnswerTest, on H2 told to sort nulls as
// PostgreSQL does), which -Pcode=today leaves out.
tasks.test {
    if (providers.gradleProperty("code").orNull == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
}

tasks.check {
    dependsOn("integrationTest")
}
