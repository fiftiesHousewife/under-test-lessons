# Shared slide: how we build

Included by every lesson with `Include: how-we-build`, as the last intro slide.

## intro: How we build
- Gradle rather than Maven, with build files written in Kotlin
- Every library version in one file, the version catalogue: an upgrade is a one-line change
- The rules every module shares come from one published plugin, which each module applies
Picture: blocks
Say: Before we get to the code, [pause 600ms] a quick word on how we build these examples. We use Gradle rather than
  Maven, and write our build files in Kotlin. Every library version sits in a single file, the version catalogue, so
  upgrading a library is a one-line change. And the rules that every module shares, such as code-quality checks and test
  settings, come from one published plugin. Each module applies it, instead of one root build file reaching into every
  sub-project. That keeps each module's own build file short.
