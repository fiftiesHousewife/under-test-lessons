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

## intro: What is a contract test?
- Two teams' services talk to each other: one calls, one answers
- A contract test checks both sides keep to one written agreement about those requests and answers
- The calling team, the consumer, writes the agreement; the answering team, the provider, checks it in its own build
Picture: plug
Say: When two teams' services talk to each other, one calls and the other answers. A contract test checks that both
  sides keep to one written agreement about those requests and answers. The team that calls, the consumer, writes the
  agreement down. The team that answers, the provider, checks it in its own build, every time it changes.

## intro: Our example: an order screen and a stock service
- The order screen asks the stock service how many of an item are available, by the item's code
- The answer has the code, and a whole number called available
- An item nobody stocks gets 404
Picture: basket
Say: Our example is a shop. One team runs the order screen. Before it takes an order, it asks the stock service how
  many of an item are available, by the item's code. Another team runs the stock service. Its answer has the item's
  code, and a whole number called available. And when nobody stocks the item, the answer is 404.

## intro: Why each team's own tests aren't enough
- Each team tests its side against what it believes the other side does
- A stand-in written by the caller agrees with the caller, always
- So both builds pass, and the break shows up only when the two services meet
Picture: magnifier
Say: Why aren't each team's own tests enough? [pause 1600ms] Each team tests its side against what it believes the
  other side does. A stand-in written by the calling team agrees with the calling team, always. So both builds pass,
  and the break shows up only when the two services finally meet, in a shared test environment, or in production.

## intro: How Pact works
- The consumer's test lists each request it makes and the answer it needs, with rules such as "any whole number"
- Pact stands in for the provider with exactly those answers, and writes them to a pact file
- The provider's test replays every request in the pact against the real service, and compares the answers
Picture: robot
Say: Pact works in two halves. The consumer's test lists each request it makes, and the answer it needs, with rules
  such as "any whole number". Pact stands in for the provider with exactly those answers while the consumer's test
  runs, and writes them to a file, the pact. The provider's test then replays every request in the pact against the
  real service, and compares each answer with the rules.

## intro: How the pact travels: the package repository
- The consumer's build publishes the pact file as a versioned artifact, beside its other build outputs
- The provider's build fetches the consumer versions it supports, and verifies each one in its own CI
- No new service to run; a Pact Broker only where the platform team hosts one
- Pact JVM 4.7 needs Java 17 or later; 4.5 needs Java 11; Java 8 code bases need the older 4.1 line
Picture: tag
Say: How does the pact get from one team to the other? [pause 1600ms] Through the company's package repository,
  where builds already publish their libraries. The consumer's build publishes the pact file as a versioned artifact.
  The provider's build fetches the consumer versions it supports, such as the newest one, and each one in production,
  and verifies them in its own continuous integration, the build that runs on every change. There is no new service to
  run. A Pact Broker adds more, but it is a service someone must host, so use one only where the platform team runs
  it. The versions of Pact JVM for older Java are on the slide.

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
Picture: globe
Say: To find out more, start with Pact's own documentation, which also says when Pact is not a good fit. The links
  on screen also take you to Pact JVM, to the original article on consumer-driven contracts, to the toolkit's guide to
  sharing pacts without a broker, and to this lesson's test code and examples, to run yourself.
