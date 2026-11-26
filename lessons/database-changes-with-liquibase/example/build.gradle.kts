plugins {
    alias(libs.plugins.java.conventions)
}

// Database changes with Liquibase: every change in the changelog applied to real PostgreSQL in a test, before production.
// Container tests live in their own suite, integrationTest, and skip themselves where there is no container runtime.
//   ./gradlew :lessons:database-changes-with-liquibase:test integrationTest -Pcode=today   today's change: PostgreSQL refuses it
//   ./gradlew :lessons:database-changes-with-liquibase:test integrationTest                the fixed change: both databases take it
dependencies {
    implementation(libs.liquibase.core)
}

testing {
    suites {
        named<JvmTestSuite>("test") {
            dependencies {
                implementation(libs.h2)
            }
        }
        // The plugin's integrationTest suite, which check runs, with the unit tests' libraries; it adds the containers.
        named<JvmTestSuite>("integrationTest") {
            dependencies {
                implementation(libs.testcontainers.postgresql)
                implementation(libs.testcontainers.junit.jupiter)
                implementation(libs.postgresql)
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

// `-Pcode=answer` runs today's changelog with the exercise's answer (answer/*AnswerTest, the SQL Liquibase would send
// PostgreSQL, written out with no database), which -Pcode=today leaves out.
tasks.test {
    if (providers.gradleProperty("code").orNull == "today") {
        filter { excludeTestsMatching("*AnswerTest") }
    }
}
