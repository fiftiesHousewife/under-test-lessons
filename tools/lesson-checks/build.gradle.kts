plugins {
    id("io.github.fiftieshousewife.java-conventions")
}

// Checks that the lessons and the toolkit (under-test) agree. Each lesson's lesson.yaml names the toolkit techniques it
// teaches; the catalogue's technique table, the lessons index and every link into the toolkit's techniques/ must match
// those files and the toolkit itself. The toolkit is read from a checkout: -PtoolkitDir=<path>, or a folder called
// under-test or UnderTest beside this repository. CI checks the toolkit out (see .github/workflows/build.yml); on a
// machine without it, the checks that need it are skipped with a warning.
dependencies {
    implementation(libs.json.schema.validator)
    implementation(libs.jackson3.yaml)
}

val repoRoot = rootDir
val toolkitDir = providers.gradleProperty("toolkitDir").map { repoRoot.resolve(it) }.orNull
    ?: listOf("../under-test", "../UnderTest").map { repoRoot.resolve(it) }.firstOrNull { it.resolve("techniques").isDirectory }
val onCi = providers.environmentVariable("CI").isPresent

val checkLessonFiles = tasks.register<JavaExec>("checkLessonFiles") {
    group = "verification"
    description = "Fails if lesson.yaml files, the catalogue, the lessons index and the toolkit's techniques disagree."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "io.github.fiftieshousewife.undertest.lessons.checks.LessonFileChecks"
    args(listOfNotNull(repoRoot.absolutePath, toolkitDir?.absolutePath))
    val missingOnCi = toolkitDir == null && onCi
    doFirst {
        if (missingOnCi) {
            throw GradleException("No toolkit checkout found. CI must check out under-test and pass -PtoolkitDir.")
        }
    }
}

tasks.test {
    systemProperty("lessonSchema", repoRoot.resolve("schema/lesson.schema.json").absolutePath)
}

tasks.check {
    dependsOn(checkLessonFiles)
}
