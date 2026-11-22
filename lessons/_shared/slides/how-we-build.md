# Shared slide: how we build

Included by every lesson with `Include: how-we-build`, as the last intro slide.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- Rules every module of the build shares live in build-logic, as convention plugins each module applies
Picture: blocks
Say: Before we start, a word on how we build. We use Gradle rather than Maven, and write our build files in Kotlin.
  Every library version sits in a single file, the version catalogue, so upgrading a library is a one-line change.
  And the rules that every module of the build shares, such as code-quality checks and test settings, are written
  once, in build-logic, as convention plugins. Each module applies the plugin it needs, instead of one root build file
  reaching into every sub-project. That keeps each module's own build file short, and says plainly what it uses.
