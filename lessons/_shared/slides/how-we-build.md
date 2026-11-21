# Shared slide: how we build

Included by every lesson with `Include: how-we-build`, as the last intro slide.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- Build rules every repo shares are written once, in build-logic, and applied to each repo as a plugin
Picture: blocks
Say: Before we start, a word on how we build. We use Gradle rather than Maven, and write our build files in Kotlin.
  Every library version sits in a single file, the version catalogue, so upgrading a library is a one-line change.
  And the build rules that every repo shares, such as code-quality checks and test settings, are written once, in a
  shared build-logic folder, and applied to each repo as a plugin. That keeps each repo's own build file short.
