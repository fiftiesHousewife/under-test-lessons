# Slides: architecture rules

Sources: archunit.org and github.com/TNG/ArchUnit (Apache 2.0 licence, by TNG Technology Consulting); Maven Central
(com.tngtech.archunit:archunit, version 0.5.0 published November 2017, version 1.0.0 published October 2022).

## intro: Architecture rules
- The problem: a shortcut past the shop's rules
- The fix: a test of which layer may use which
- The tool: ArchUnit
Picture: road
Say: The next stop on the journey is architecture rules: which layer of a shop's code may use which. ArchUnit is a
  library that checks how Java code is put together. With it, the rules become a test, so every build checks them. We'll
  see a shortcut that breaks the rules, and the test that catches it.

## intro: Our example: a shop in three layers
- Web: the pages people see, including an admin page
- Service: the shop's rules, such as "an order can't be free"
- Storage: where orders are kept
Picture: blocks
Say: Our example is a small shop, built in three layers. The web layer has the pages people see, including an admin
  page. The service layer holds the shop's rules, such as: an order can't be free. And the storage layer is where
  the orders are kept. Web may use the service, and only the service may use storage.

Include: how-we-build

## closing: When to use architecture rules
- When you agree how old code should be shaped, write it down as a rule
- Run the rules with the unit tests, in every build
- Keep rules few and named, so a failure says what was agreed
Picture: map
Say: When should you use architecture rules? [pause 1600ms] Whenever a team agrees how the code should be shaped, write
  it down as a rule, so the build holds the team to it. The rules are ordinary tests, so they run with the unit tests,
  every time. And keep the rules few, with clear names.

## closing: Where ArchUnit came from
- Written by TNG Technology Consulting, free under the Apache licence
- Published since 2017
- Version 1.0 came out in 2022
- Works with JUnit, and with any Java or Kotlin code
Picture: book
Say: ArchUnit was written at TNG Technology Consulting, and is free to use under the Apache licence. It has been
  published since 2017, and version one came out in 2022. It works with JUnit, and with any Java or Kotlin code.

## closing: Find out more
- https://www.archunit.org — ArchUnit, with its user guide
- {lessons-repo}/tree/main/lessons/architecture-rules — run this lesson yourself: its test code and examples
- Exercise: write a rule listing the only layers the web layer may use; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the ArchUnit user guide. The links on screen also take you to this lesson's test code
  and examples, to run yourself. And try the exercise in the README: write a rule that lists the only layers the web
  layer may use.
