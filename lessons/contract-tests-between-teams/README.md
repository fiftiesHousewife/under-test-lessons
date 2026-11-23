# Contract tests between teams

**The team that calls a service writes down what it relies on; the team that runs the service checks it in its own
build.**

When two teams' services talk to each other, each team tests its own side against what it believes the other does.
The calling team's stand-in for the other service agrees with the calling team, always, so a renamed field passes
both builds and breaks only when the two services meet. A contract test puts the agreement in one file, a pact:
[Pact JVM](https://github.com/pact-foundation/pact-jvm) runs the consumer's tests against a stand-in that answers
exactly what the pact says and writes the pact file, and the provider's tests replay every request in it against the
real service. The pact travels as a versioned artifact through the company's package repository, so there is no new
service to host.

## Run the example

```bash
./gradlew :lessons:contract-tests-between-teams:test -Pcode=today   # today's stock service: both teams' own tests pass, the pact check names the missing field "available"
./gradlew :lessons:contract-tests-between-teams:test                # the fixed stock service: every test passes
```

The example is a shop's order screen (the consumer,
[`StockClient`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/contracts/consumer/StockClient.java))
asking a stock service (the provider, on the JDK's own web server:
[`StockServer`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/StockServer.java))
how many of an item are available. In a company the two would be separate repositories; here they are two packages.

- [`TodaysStockBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/today/TodaysStockBodies.java)
  is today's code: a tidy-up renamed `available` to `quantityInStock`.
  [`AgreedStockBodies`](example/src/main/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/fixed/AgreedStockBodies.java)
  is the fix: it keeps `available` and sends the new name beside it, until no supported consumer version reads the old
  one.
- The "before" is each team's own tests:
  [`StockClientTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/consumer/StockClientTest.java)
  runs the order screen against a
  [stand-in its own team wrote](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/consumer/HandWrittenStockService.java),
  and [`StockServiceTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/StockServiceTest.java)
  checks the stock service's status code and item code. Both pass on today's code.
- The "after" is the pact, checked from both sides.
  [`StockClientPactTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/consumer/StockClientPactTest.java)
  writes down each answer the order screen relies on, runs the client against Pact's stand-in, and writes the pact to
  `example/build/pacts/`.
  [`StockServicePactTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/StockServicePactTest.java)
  reads the published pact with `@PactFolder` and replays it against the real stock service; on today's code it fails,
  saying the answer is missing the key `available`.

## How the pact is shared: a versioned artifact, without a broker

The provider never reads the consumer's build folder. It verifies a published version of the pact, as it would depend
on any other library:

1. **The consumer's build publishes the pact file** to the company's package repository (Nexus, Artifactory or
   similar) as an artifact versioned with the consumer, for example `order-screen-pacts:1.0.0`, with the same Maven or
   Gradle publishing it already uses for libraries.
2. **The provider's build lists the consumer versions it supports** (the newest on the main branch, and each version
   the release records show as deployed), fetches them as ordinary dependencies, and verifies each one in its own CI.
   The list lives in the provider's repository and changes by pull request.
3. **A Pact Broker** (or PactFlow) adds a record of which versions work together, but it is a service someone must
   host and own. Use one only where the platform team runs it; the tests don't change if you move to one later.

In this lesson the folder
[`example/package-repository/order-screen-pacts/1.0.0/`](example/package-repository/order-screen-pacts/1.0.0/order-screen-stock-service.json)
stands in for the package repository: it holds the order screen's published pact, and it is the folder the provider's
test reads. The consumer's test writes a fresh pact to `example/build/pacts/` on every run and never touches the
published copy, so runs are repeatable and need no network. To publish a changed pact,
`./gradlew :lessons:contract-tests-between-teams:publishPact` copies it into the folder, standing in for a real
publication. The build also sets `pact_do_not_track`, so Pact JVM sends no usage figures over the internet.

Pact JVM 4.7, used here, needs Java 17 or later. On older code bases, use the 4.5 line for Java 11, and the 4.1 line
for Java 8, which Pact no longer supports actively.

## Exercise

The order screen's team has published version 1.1.0 of its pact. Beside the kettle, it now writes down what the order
screen relies on when nobody stocks an item: a 404, with an error message. The stock service's build should verify the
new version too, with a state for the new request.

1. Read [version 1.1.0 of the pact](example/package-repository/order-screen-pacts/1.1.0/order-screen-stock-service.json),
   and find the state its new request names: `nothing called TOASTER-99 is stocked`.
2. Add a test class beside [`StockServicePactTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/StockServicePactTest.java)
   that reads that folder with `@PactFolder`, and give it a `@State` method for each state the pact names.
3. Compare with the answer, [`NewPactVersionAnswerTest`](example/src/test/java/io/github/fiftieshousewife/undertest/techniques/contracts/provider/answer/NewPactVersionAnswerTest.java),
   which runs against today's stock service with:

```bash
./gradlew :lessons:contract-tests-between-teams:test -Pcode=answer   # today's stock service: 2 of 7 tests fail; the new request for an item nobody stocks passes, the kettle still fails
```

## Watch it, or present it

- **Slide deck:** [open it](https://fiftieshousewife.github.io/under-test-lessons/contract-tests-between-teams.html)
  (its source is [`media/contract-tests-between-teams.deck.html`](media/contract-tests-between-teams.deck.html)).
  Arrow keys move between slides, N shows the speaker notes, and printing gives one slide per page.
- **Video:** made from [the script](media/contract-tests-between-teams.tape),
  [the narration](media/contract-tests-between-teams.narration.md) and
  [the slides](media/contract-tests-between-teams.slides.md) with
  `./gradlew :production:video-render:renderLessonVideo -Plesson=contract-tests-between-teams`.

## When to use it

- **Between services in one company,** where both teams agree to run the checks: the provider team's agreement comes
  first.
- **Before renaming or retyping a field:** the pacts say which consumers still read it.
- **Pin the consumer versions you support** in the provider's build, and update the list by pull request from the
  release records.
- **Not for a supplier's API or a public one,** where the other side won't verify: use
  [recorded HTTP responses](../recorded-http-responses/README.md) with scheduled checks against the supplier's
  sandbox, or [responses checked against the OpenAPI document](../openapi-response-checks/README.md).

## Find out more

- [Pact's documentation](https://docs.pact.io), including
  [what Pact is and isn't good for](https://docs.pact.io/getting_started/what_is_pact_good_for) and
  [sharing pacts](https://docs.pact.io/getting_started/sharing_pacts)
- [Pact JVM](https://github.com/pact-foundation/pact-jvm), with its table of which version runs on which Java, and its
  [JUnit 5 provider guide](https://docs.pact.io/implementation_guides/jvm/provider/junit5)
- Ian Robinson, [Consumer-Driven Contracts: A Service Evolution Pattern](https://martinfowler.com/articles/consumerDrivenContracts.html)
- The toolkit's [contract-testing technique](https://github.com/fiftiesHousewife/under-test/blob/main/techniques/contract-testing/technique.yaml)
  and its decision guide,
  [Contract tests share pacts as versioned artifacts through the package repository, without a broker](https://github.com/fiftiesHousewife/under-test/blob/main/decision-guides/0037-contract-tests-share-pacts-as-versioned-artifacts-through-the-package-repository-without-a-broker.md)
