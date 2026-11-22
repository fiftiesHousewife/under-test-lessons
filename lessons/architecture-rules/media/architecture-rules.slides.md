# Slides: architecture rules

Sources: archunit.org and github.com/TNG/ArchUnit (Apache 2.0 licence, by TNG Technology Consulting); Maven Central
(com.tngtech.archunit:archunit, version 0.5.0 published November 2017, version 1.0.0 published October 2022).

## intro: A shortcut past the shop's rules
- A shop's admin page reads the stored orders directly, skipping the service
- It still shows the right answer: Orders: 2
- So the unit tests pass, and nobody notices the shortcut
- Each shortcut skips the shop's rules, and makes the next change harder
Picture: bug
Say: Here is the problem. A shop's admin page reads the stored orders directly, and skips the layer that holds the
  shop's rules. It still shows the right answer: 2 orders. So the unit tests pass, and a review can easily miss it.
  But every shortcut like this skips the shop's rules, and makes the next change harder. In old code, these shortcuts
  pile up quietly.

## intro: Our example: a shop in three layers
- Web: the pages people see, including an admin page
- Service: the shop's rules, such as "an order must cost something"
- Storage: where orders are kept
Picture: blocks
Say: Our example is that small shop, built in three layers. The web layer has the pages people see, including the
  admin page. The service layer holds the shop's rules, such as: an order must cost something. And the storage layer
  is where the orders are kept. Web may use the service, and only the service may use storage.

## intro: The fix: a test of which layer may use which
- An architecture rule checks how the code is put together, not what it does
- Ours: only the service layer may use storage
- It reads the compiled classes, so it sees every call, field and parameter
Picture: layers
Say: The fix is a test of which layer may use which. This is called an architecture rule: a test that checks how the
  code is put together, rather than what it does. Ours says that only the service layer may use storage. The rule
  reads the compiled classes, so it sees every call, every field and every parameter, and nothing slips past.

## intro: How ArchUnit looks
- Name the layers: .layer("Storage").definedBy(BASE + ".storage..")
- Say who may use whom: .whereLayer("Storage").mayOnlyBeAccessedByLayers("Service")
- Today it names each break: Method <web.today.TodaysSummaryPage.summary()> calls method <storage.OrderStore.count()>
Picture: robot
Say: Here is how ArchUnit, a Java library for these rules, looks. It imports the compiled classes of the packages you
  name. You name each layer by its package, in plain Java. Then you say which layer may use which: storage may only be
  used by the service. Today the rule fails, and names every break: the admin page's constructor, its field, and its
  call that counts the stored orders.

Include: how-we-build

## closing: When to use architecture rules
- When you agree how old code should be shaped, write it down as a rule
- On old code, freeze today's breaks so only new ones fail the build
- Keep rules few and named, so a failure says what was agreed
Picture: map
Say: When should you use them? [pause 1600ms] Whenever a team agrees how the code should be shaped, write it down as a
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
- {lessons-repo} — run this lesson yourself: its test code and examples
Picture: globe
Say: To find out more, start with the ArchUnit user guide. The links on screen also take you to this lesson's test
  code and examples, to run yourself.
