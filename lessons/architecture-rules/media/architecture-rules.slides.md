# Slides: architecture rules

Sources: archunit.org and github.com/TNG/ArchUnit (Apache 2.0 licence, by TNG Technology Consulting); Maven Central
(com.tngtech.archunit:archunit, version 0.5.0 published November 2017, version 1.0.0 published October 2022).

## intro: What is an architecture rule?
- A test that checks how the code is put together, not what it does
- For example: the web layer must never use storage directly
- It reads the compiled classes, so it sees every call, field and parameter
Picture: layers
Say: An architecture rule is a test that checks how the code is put together, rather than what it does. For example:
  the web layer must never use storage directly. The rule reads the compiled classes, so it sees every call, every
  field and every parameter, and nothing slips past.

## intro: Our example: a shop in three layers
- Web: the pages people see, including an admin page
- Service: the shop's rules, such as "an order must cost something"
- Storage: where orders are kept
Picture: blocks
Say: Our example is a small shop, built in three layers. The web layer has the pages people see, including an admin
  page. The service layer holds the shop's rules, such as: an order must cost something. And the storage layer is where
  the orders are kept.

## intro: What we'll compare
- The code under test: the admin page, which reads storage directly today
- Unit tests: check the page shows the right number of orders
- An architecture rule: checks which layer may use which
Picture: scales
Say: Here is what we will compare. The code under test is the admin page, which reads storage directly today.
  [pause 300ms] The unit tests check that the page shows the right number of orders. [pause 300ms] And the
  architecture rule checks which layer may use which.

## intro: Why unit tests can't see this
- A shortcut past the service still gives the right answer, for now
- Each shortcut skips the rules, and makes the next change harder
- Shortcuts pile up quietly; reviews miss them
Picture: magnifier
Say: Why do we need this? [pause 1s] A shortcut past the service layer still gives the right answer, for now. So the
  unit tests pass. But every shortcut skips the shop's rules, and makes the next change harder. In old code these
  shortcuts pile up quietly, and reviews miss them.

## intro: How ArchUnit works
- It imports the compiled classes of the packages you name
- You describe the rule in plain Java: layers, and who may use whom
- A failure lists every class, field and call that breaks the rule
Picture: robot
Say: ArchUnit imports the compiled classes of the packages you name. You describe the rule in plain Java: the layers,
  and which layer may use which. When the rule fails, it lists every class, field and call that breaks it.

Include: how-we-build

## closing: When to use architecture rules
- When you agree how old code should be shaped, write it down as a rule
- On old code, freeze today's breaks so only new ones fail the build
- Keep rules few and named, so a failure says what was agreed
Picture: map
Say: When should you use them? [pause 1s] Whenever a team agrees how the code should be shaped, write it down as a
  rule, so the build keeps the agreement. On old code with many breaks already, ArchUnit can freeze today's breaks, so
  only new ones fail the build. And keep the rules few, with clear names.

## closing: Where ArchUnit came from
- Written by TNG Technology Consulting, free under the Apache licence
- Published since 2017; version 1.0 came out in 2022
- Works with JUnit, and with any Java or Kotlin code
Picture: book
Say: ArchUnit was written at TNG Technology Consulting, and is free to use under the Apache licence. It has been
  published since 2017, and version one came out in 2022. It works with JUnit, and with any Java or Kotlin code.

## closing: Find out more
- https://www.archunit.org — ArchUnit, with its user guide
- https://github.com/fiftiesHousewife/under-test-lessons — this lesson, to run yourself
- https://github.com/fiftiesHousewife/under-test — the toolkit these lessons belong to
Picture: globe
Say: To find out more, start with the ArchUnit user guide. The links on screen also take you to this lesson's code,
  and to the toolkit these lessons belong to.
