pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    // Where Gradle downloads a JDK 25 if none is installed.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
    }
}

rootProject.name = "under-test-lessons"

// Each lessons/<id>/example becomes the project :lessons:<id>.
rootDir.resolve("lessons").listFiles()?.sorted()?.forEach { dir ->
    if (dir.resolve("example/build.gradle.kts").isFile) {
        include(":lessons:${dir.name}")
        project(":lessons:${dir.name}").projectDir = dir.resolve("example")
    }
}
// Tools for lesson videos: checks, recording, captions, voice and assembly.
include(":lessons:video-tools")
project(":lessons:video-tools").projectDir = rootDir.resolve("lessons/_shared/video-tools")
// The tools that make videos and their voice run outside a company only; a copy without production/ still builds.
if (rootDir.resolve("production/video-render").isDirectory) {
    include(":production:video-render")
    project(":production:video-render").projectDir = rootDir.resolve("production/video-render")
    include(":production:voice-review")
    project(":production:voice-review").projectDir = rootDir.resolve("production/voice-review")
}
// Checks that lesson.yaml files, the catalogue, the lessons index and the toolkit's techniques agree.
include(":tools:lesson-checks")
