plugins {
    id("io.github.fiftieshousewife.java-conventions")
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
        val test by getting(JvmTestSuite::class) {
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
    outputs.upToDateWhen { false } // a lesson always reruns its tests, so switching versions never shows a stale result
}

tasks.check {
    dependsOn("integrationTest")
}
