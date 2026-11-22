# Narration: contract tests between teams

One item per scene of [contract-tests-between-teams.tape](contract-tests-between-teams.tape). The intro and closing
slides have their own narration, in [contract-tests-between-teams.slides.md](contract-tests-between-teams.slides.md).
Written to be spoken.

1. Step one. Add Pact JVM to the version catalogue. Version 4.7 runs on Java 17 or later, so it suits our Java 25
   build. Code bases on older Java need an older line.
2. Step two. Add Pact's two JUnit libraries as test dependencies. One is for the team that calls the service, the
   consumer. The other is for the team that runs it, the provider. In a company they live in two repositories. Here
   they share one lesson.
3. Here is the code under test, starting with the consumer. The order screen asks the stock service how many of an
   item are available, by the item's code. It reads two fields from the answer: the code, and a whole number called
   available. When the answer is 404, there is no such item.
4. And the provider: today's stock service. A tidy-up renamed the field available to quantity in stock. Nobody told
   the order screen's team.
5. These are [pause 300ms] the order screen's weak tests. They run against a stand-in its own team wrote, which
   answers exactly what they expect: a field called available. So they pass, whatever the real stock service sends.
6. And the stock service's own weak tests. They check the status code and the item's code. Nothing in them says which
   fields the order screen reads, so they pass after the rename too.
7. Now, [pause 300ms] the strong tests, starting with the consumer. The order screen's team writes down each answer it
   relies on, as an interaction in a pact. Given three kettles in stock, a request for the kettle gets 200, with the
   code, and any whole number called available. Pact stands in for the stock service with exactly these answers, runs
   the client against them, and writes the pact file.
8. This is the pact file, the agreement both teams check. The order screen's build publishes it to the company's
   package repository, as a versioned artifact, numbered with the order screen's own version. Here, a folder stands in
   for the package repository. Look at the field available, and the rule that it must be a whole number.
9. And the provider's strong test. It reads the pact from that folder, starts the real stock service, and replays each
    interaction against it. A provider state, such as three kettles in stock, is a method that sets up the data the
    interaction needs.
10. We run every test against today's stock service. Both teams' weak tests pass, and so does the order screen's pact
    test. But the stock service fails the pact, and names the field. The answer has no field called available, only
    quantity in stock.
11. Now the fixed stock service. It keeps the field available, which the pact names, and sends the new name beside it,
    until no supported version of the order screen reads the old one. We run the same tests. [pause 1s] Every test
    passes.
