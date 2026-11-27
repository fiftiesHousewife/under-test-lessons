# Narration: contract tests between teams

One item per scene of [contract-tests-between-teams.scenes](contract-tests-between-teams.scenes). The intro and closing
slides have their own narration, in [contract-tests-between-teams.slides.md](contract-tests-between-teams.slides.md).
Written to be spoken.

1. Step one. Add Pact JVM to the version catalogue. Version 4.7 runs on Java 17 or later, so it suits the lesson's Java
   25 code. Code bases still on Java 8 need the older 4.1 line.
2. Step two. Add Pact's two JUnit libraries as test dependencies. One is for the team that calls the service, the
   consumer. The other is for the team that runs it, the provider. In a company they live in two repositories. Here
   they share one lesson.
3. Here is the code under test, starting with the consumer, the order screen's client. It asks for the item by its
   code. A 404 means no such item. Otherwise it reads two fields from the answer: the code, and the whole number
   called available.
4. And the provider: today's stock service. A tidy-up renamed the field called available. Its new name is quantity in
   stock. The order screen's team never heard about it.
5. These are the order screen's weak tests. They run against a stand-in its own team wrote, which
   answers exactly what they expect: a field called available. So they pass, whatever the real stock service sends.
6. And the stock service's own weak tests. They check the status code and the item's code. Nothing in them says which
   fields the order screen reads, so they pass after the rename too. The break would show only when the two services
   meet.
7. The strong tests start with the consumer. Two annotations at the top hand the test to Pact, and name the provider,
   the stock service. The order screen's team writes down each answer it relies on, as an interaction in a pact. Given
   three kettles in stock, a request for the kettle gets a 200 status code. Below it, Pact stands in for the stock
   service with exactly these answers, runs the client against them, and writes the pact file.
8. This is the pact file, the agreement both teams check. The answer holds the field available, and a matching rule
   says it must be a whole number. Below the pact, a copy step in the build file stands in for publishing it. In a
   company, the order screen's build publishes the pact to the package repository, as a versioned artifact, numbered
   with the order screen's own version. Builds already publish their libraries there, so there is no new service to
   run. Here, a folder stands in for the package repository.
9. And the provider's strong test. It names the version of the pact it checks, 1.0.0. That is how the provider's build
    pins each consumer version it supports. It reads the pact from that folder, starts the real stock service, and
    replays each interaction against it. A provider state, such as three kettles in stock, is a method that sets up the
    data the interaction needs.
10. We run every test against today's stock service. Both teams' weak tests pass, and so does the order screen's pact
    test. But the stock service fails the pact, and names the field. The answer has no field called available, only
    quantity in stock.
11. Here is the fixed stock service. It keeps the field available, which the pact names, and sends the new name beside
    it, until no supported version of the order screen reads the old one. We run the same tests. [pause 1s] Every test
    passes.
