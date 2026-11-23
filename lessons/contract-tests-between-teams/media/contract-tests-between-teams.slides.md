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
Evolution Pattern", 12 June 2006; the toolkit's contract-testing technique and its decision guide "Contract tests share
pacts as versioned artifacts through the package repository, without a broker".

## intro: A renamed field passes both teams' builds
- A tidy-up in the stock service renames available to quantityInStock
- The order screen's tests pass: they run against a stand-in their own team wrote
- The stock service's tests pass: they check the status code and the item's code
- The break shows only when the two services meet
Picture: bug
Say: Here is the problem. A tidy-up in a shop's stock service renames the field that says how many kettles are
  available. The order screen, run by another team, still looks for the old name. Both teams' builds pass. The order
  screen's tests run against a stand-in for the stock service that their own team wrote, so it agrees with them,
  always. The stock service's tests check only the status code and the item's code. So the break shows up only when the
  two services finally meet, in a shared test environment, or in production.

## intro: Our example: an order screen and a stock service
- The order screen asks the stock service how many of an item are available, by the item's code
- The answer has the code, and a whole number called available
- An item nobody stocks gets 404
Picture: basket
Say: Our example is that shop. One team runs the order screen. Before it takes an order, it asks the stock service how
  many of an item are available, by the item's code. Another team runs the stock service. Its answer has the item's
  code, and a whole number called available. And when nobody stocks the item, the answer is 404.

## intro: The fix: one agreement, checked from both sides
- The order screen, the consumer, writes down each answer it relies on: the agreement, a pact
- The stock service, the provider, checks the pact in its own build, on every change
- The pact travels as a versioned artifact through the company's package repository
Picture: plug
Say: The fix is a contract test: one written agreement, checked from both sides. The team that calls, the consumer,
  writes down each request it makes and the answer it relies on. Pact, the tool, calls that agreement a pact. The team
  that answers, the provider, checks the pact in its own build, every time it changes. The pact travels as a versioned
  artifact, a numbered build output, through the company's package repository, where builds already publish their
  libraries. So there is no new service to run.

## intro: How Pact looks
- The order screen's test: .integerType("available", 3), any whole number, named available
- The stock service's test: @Provider("stock-service") @PactFolder("package-repository/order-screen-pacts/1.0.0")
- Today: body: $ Actual map is missing the following keys: available
- Pact JVM 4.7 needs Java 17 or later; Java 8 code bases need the older 4.1 line
Picture: robot
Say: Here is how Pact looks. The order screen's test says the answer needs a field called available, holding any whole
  number. Pact stands in for the stock service with exactly that answer while the test runs, and writes the pact file.
  The stock service's test reads version 1.0.0 of the published pact, and replays every request in it against the real
  service. Today it fails, and says the answer is missing the key, available. One more thing: Pact JVM 4.7 needs Java
  17 or later, and code bases still on Java 8 need the older 4.1 line.

Include: how-we-build

## closing: When to use contract tests
- Between services in one company, where both teams agree to run the checks
- Pin each consumer version you support in the provider's build, and update the list by pull request
- Before renaming or retyping a field: the pact says who still reads it
- Not for a supplier's API or a public one: check those against recorded answers or a published OpenAPI document
Picture: map
Say: When should you use contract tests? [pause 1600ms] Between services in one company, where both teams agree to
  run the checks. Keep the list of consumer versions you support in the provider's build, and change it by pull
  request. Check the pacts before renaming or retyping a field, since they say who still reads it. They don't suit a
  supplier's API, or a public one, where the other side won't run them. Check those against recorded answers, or
  against a published OpenAPI document.

## closing: Where it came from
- Ian Robinson described consumer-driven contracts in 2006
- Pact was first written at realestate.com.au in 2013, by a team that included DiUS developers
- DiUS consultants started Pact JVM on a hack day; it is free under the Apache licence
Picture: book
Say: Ian Robinson described consumer-driven contracts in 2006: the consumers say what they need, and the provider
  checks it gives them that. Pact was first written at realestate.com.au, an Australian property site, in 2013, by a
  team that included developers from the consultancy DiUS. DiUS consultants started Pact JVM on a hack day. It is
  free, under the Apache licence.

## closing: Find out more
- https://docs.pact.io — Pact's documentation, with when Pact is and isn't a good fit
- https://github.com/pact-foundation/pact-jvm — Pact JVM, and which version runs on which Java
- https://martinfowler.com/articles/consumerDrivenContracts.html — consumer-driven contracts
- https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0037-contract-tests-share-pacts-as-versioned-artifacts-through-the-package-repository-without-a-broker.md — the toolkit's guide to sharing pacts without a broker
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: verify version 1.1.0 of the pact, which adds an item nobody stocks; the README has the steps and the answer
Picture: globe
Say: To find out more, start with Pact's own documentation, which also says when Pact is not a good fit. The links
  on screen also take you to Pact JVM, to the original article on consumer-driven contracts, to the toolkit's guide to
  sharing pacts without a broker, and to this lesson's test code and examples, to run yourself. And try the exercise
  in the README: verify version 1.1.0 of the pact, which adds an item nobody stocks.
