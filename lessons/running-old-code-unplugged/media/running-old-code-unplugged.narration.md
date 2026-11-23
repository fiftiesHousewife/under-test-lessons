# Narration: running old code unplugged

One item per scene of [running-old-code-unplugged.tape](running-old-code-unplugged.tape). The intro and closing
slides have their own narration, in [running-old-code-unplugged.slides.md](running-old-code-unplugged.slides.md).
Written to be spoken.

1. Step one. The airgap agent and its JUnit listener go in the version catalogue, under one version. Both come from Maven Central.
2. Step two. The build file adds the agent to the tests. Java starts an agent only when it is named on its command line, so the test task names the agent's jar, the file for its report, and the service's own packages. The JUnit listener tells the agent which test is running.
3. Here are the service's settings, as they came to us. Every address is production's: the database, the courier's rates, and the payment gateway. In the lesson, each name ends in invalid, which can never be looked up.
4. Here is the code under test. A product's price comes from the shop's database. The delivery charge comes from the courier's rates service, over HTTP, through the JDK's own connection class, as in many old services.
5. Now, [pause 300ms] the service's own tests. Each one starts the whole service, with its settings, and asks for a price, a delivery charge, or a quote. Nothing in them says where the service connects.
6. We run the tests under the agent, with only the service's settings. Every test fails, as on an unplugged machine. The summary lists each machine they tried: the database, and the courier's rates. For each, the agent's report names the tests and the line of code. The summary adds the setting that holds the address.
7. Now the fix: a test profile. When it is on, Spring Boot reads application-test.yml on top of the service's settings, and the build turns it on for the tests. The database becomes H2, held in memory and built from the shop's real schema. The courier's rates point at a stand-in on this machine.
8. The courier's stand-in is WireMock, a small web server, started on this machine under the test profile only. It answers from mapping files, here the rate for central London. Calls to this machine are always allowed.
9. We run the same tests, with the test profile. [pause 1s] All 5 tests pass, and the agent refused nothing. What the report listed is the start of the service's list of connections.
