# Slides: contract tests between teams

Sources: docs.pact.io, its introduction, "What is Pact good for?", "Sharing pacts" and history pages (Pact was first
written at realestate.com.au in 2013 by a team including a DiUS developer; DiUS consultants started pact-jvm on a hack
day; provider states; Pact suits services inside one organisation whose teams both run it, and not public APIs or teams
who won't use it; the Pact Broker publishes pacts, records verification results and shows which versions work together;
alternatives without a broker include publishing pacts as build artifacts); docs.pact.io's Pact JVM JUnit 5 provider
page (@Provider, @PactFolder, @TestTemplate with PactVerificationInvocationContextProvider, @State; anonymous usage
tracking turned off with the pact_do_not_track setting); github.com/pact-foundation/pact-jvm, its README (supported JDK
table: 4.7 and 4.6 need JDK 17 or later, 4.7 tested up to 27; 4.5 needs JDK 11 or later; 4.1, for JDK 8 to 12, is no
longer actively supported) and its Apache 2 licence; Maven Central (au.com.dius.pact.consumer:junit5 and
au.com.dius.pact.provider:junit5, 4.7.7); martinfowler.com, Ian Robinson's "Consumer-Driven Contracts: A Service
Evolution Pattern", 12 June 2006.

## intro: Contract tests between teams
- The problem: A renamed field passes both teams' builds
- The fix: one agreement, checked by both
- The tool: Pact JVM
Picture: road
Say: In this lesson, we look at contract tests between teams. Pact is a tool that writes down what one service expects
  of another. Both teams' builds check that one agreement.

## intro: Our example: two teams, two services
- The order screen asks the stock service how many of an item are available, by the item's code
- The answer has the code, and a whole number called available
- An item the shop doesn't stock gets 404
Picture: basket
Say: Our example is the shop. One team runs the order screen. Before it takes an order, it asks the stock service how
  many of an item are available, by the item's code. Another team runs the stock service. Its answer has the item's
  code, and a whole number called available. And when the shop doesn't stock the item, the answer is 404.

Include: how-we-build

## closing: When to use contract tests
- Between services in one company, where both teams agree to run the checks
- Pin each consumer version you support in the provider's build, and update the list by pull request
- Before renaming or retyping a field: the pact says who still reads it
- Not for a supplier's API or a public one: check those against recorded answers or a published OpenAPI schema
Picture: map
Say: When should you use contract tests? [pause 1600ms] Between services in one company, where both teams agree to run
  the checks. Keep the list of consumer versions you support in the provider's build, and change it by pull request.
  Check the pacts before renaming or retyping a field, since they say who still reads it. Contract tests don't suit a
  supplier's API, or a public one, where the other side won't run them. Check [pause 400ms] those [pause 400ms] against
  recorded answers, or [pause 400ms] against a published OpenAPI schema.

## closing: Where it came from
- Ian Robinson described consumer-driven contracts in 2006
- Pact was first written at realestate.com.au in 2013, by a team that included DiUS developers
- DiUS consultants started Pact JVM on a hack day
- It is free under the Apache licence
Picture: book
Say: Ian Robinson described consumer-driven contracts in 2006: the consumers say what they need, and the provider checks
  it gives them that. Pact was first written at realestate.com.au, an Australian property site, in 2013, by a team that
  included developers from the consultancy DiUS. DiUS consultants started Pact JVM on a hack day. It is free, under the
  Apache licence.

## closing: Find out more
- https://docs.pact.io — Pact's documentation, with when Pact is and isn't a good fit
- https://github.com/pact-foundation/pact-jvm — Pact JVM, and which version runs on which Java
- https://martinfowler.com/articles/consumerDrivenContracts.html — consumer-driven contracts
- {lessons-repo}/tree/main/lessons/contract-tests-between-teams — run this lesson yourself: its test code and examples
- Exercise: verify version 1.1.0 of the pact, which adds an item the shop doesn't stock; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Pact's own documentation, which also says when Pact is not a good fit. The links on
  screen also take you to Pact JVM, and the original article on consumer-driven contracts. And try the exercise in the
  README: verify version 1.1.0 of the pact, which adds an item the shop doesn't stock.
