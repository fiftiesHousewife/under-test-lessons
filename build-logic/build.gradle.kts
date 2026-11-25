plugins {
    `kotlin-dsl`
}

dependencies {
    // The shared code-quality standards, published to Maven Central; the lesson plugin applies them.
    implementation(libs.java.conventions.plugin)
}
