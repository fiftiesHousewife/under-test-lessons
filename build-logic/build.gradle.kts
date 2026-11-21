plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.pitest.plugin)
    implementation(libs.gradle.spotbugs.plugin)
    implementation(libs.gradle.forbiddenapis.plugin)
    implementation(libs.gradle.versions.plugin)
}
