# Slides: Running old code unplugged

Sources: the airgap agent's README and design (github.com/fiftiesHousewife/under-test-airgap: a Java agent named on
the JVM's command line; needs no administrator rights or installs; refuses host lookups, TCP and UDP connections and
starting programs, with the exception an offline machine throws; calls to the same machine go ahead; one line of JSON
per call in build/airgap/test/calls.jsonl, naming the test and the caller; naming the Spring setting, with its file and
line, listed as still to come; guards against accidental calls, not hostile code; the Security Manager disabled in
recent JDKs; WebMock and pytest-socket do this job for Ruby and Python); Maven Central (airgap-agent 0.1.0;
spring-context 3.1.0, the first Spring with profiles, published in December 2011); OpenJDK's JEP 411 (the Security
Manager deprecated for removal in JDK 17, 2021) and JEP 486 (permanently disabled in JDK 24, 2025); the JDK's
java.lang.instrument documentation (Java agents, since Java 5, 2004); Spring Boot's reference documentation
(profile-specific files such as application-test.yml); WireMock's documentation (proxying with proxyBaseUrl, record
and playback); the toolkit's decision guide, Old code runs first only where production cannot be reached; this
lesson's runs.

## intro: The tests point at production
- An old quote service's settings name production's database, courier and payment gateway
- Its tests start the whole service, with those settings
- Run on a laptop on the company network, they could read or change real data
- No administrator rights and no containers to cut the laptop off
Picture: server
Say: Here is the problem. We have been given an old quote service, and we have never seen it run. Its
  settings name production's database, the courier's rates service and a payment gateway. Its tests start the whole
  service with those settings. On a laptop on the company network, they could read or change real data. And
  containers and firewall rules need rights that most company laptops don't give.

## intro: Our example: a shop's quote service
- What a product costs, from the shop's database
- Plus delivery to a postcode area, from the courier's rates service
- It also takes card payments, through a payment gateway
- 3 tests of its own, which start the whole service
Picture: basket
Say: Our example is that shop's quote service, built on Spring Boot. It works out what a product costs,
  from the shop's database, plus delivery to a postcode area, from the courier's rates service. It also takes card
  payments. And it has 3 tests of its own, which start the whole service.

## intro: The fix: run it unplugged, then give each address a stand-in
- A Java agent: a jar named on the JVM's command line, which changes the JDK as it loads
- The airgap agent refuses every call to another machine, as an unplugged machine would
- Each refused call is reported: the address, the test, and the line of code
- A test profile then gives each address a stand-in, until the report is clean
Picture: plug
Say: The fix is to run the tests unplugged. A Java agent is a jar named on the command line that starts
  Java, which changes the JDK's own classes as they load. The airgap agent refuses every call to another machine,
  with the error an unplugged machine would give, and reports each one: the address, the test, and the line of code.
  Then a test profile, a second settings file for the tests, gives each address a stand-in, until the report is
  clean.

## intro: How it looks
- -javaagent:airgap-agent.jar=report=build/airgap/test/calls.jsonl
- rates.prod.example.invalid:443: refused by the airgap agent
- One line of JSON per call: its kind, target, test and caller
- application-test.yml: url: jdbc:h2:mem:shop
Picture: terminal
Say: How does it look? [pause 1600ms] The agent is one setting on the test JVM's command line. A refused
  call fails with an ordinary error, naming the airgap agent. The report has one line of JSON per call: its kind, its
  target, the test and the line of code. And the test profile replaces each address with a stand-in.

Include: how-we-build

## closing: From the report to a recording plan
- Each web address in the report becomes a WireMock proxy mapping
- Record once, from a test environment, never production
- Draft cleaning rules: drop passwords, key order references, never a card number
- Then the recorded HTTP responses and cleaning captured data lessons
Picture: camera
Say: The report's web addresses are also a recording plan. Each one becomes a WireMock proxy mapping,
  which passes requests on to a test environment, never production, and records the answers to replay. Beside each
  goes a draft of its cleaning rules. The lessons on recorded HTTP responses, and on cleaning captured data, take it
  from there.

## closing: When to run old code unplugged
- The first time old code runs on a developer's machine, and every time after
- The very first run in continuous integration, on machines cut off from the network
- In discovery: each line of the report is something the service depends on
- A safety net against accidental calls, not against hostile code
Picture: map
Say: When should you run old code unplugged? [pause 1600ms] Every time it runs on a developer's
  machine. The toolkit's guide puts the very first run in continuous integration, on machines cut off
  from the network. In discovery, each
  line of the report is something the service depends on. And the agent is a safety net against accidental calls,
  not against code written to get round it.

## closing: Where it came from
- Java agents arrived with Java 5, in 2004
- Java's Security Manager could refuse connections, but was deprecated in 2021 and switched off in 2025
- Ruby has WebMock and Python has pytest-socket; Java had no maintained tool
- Spring's profiles, which the test profile uses, arrived with Spring 3.1, in 2011
Picture: book
Say: Java agents arrived with Java 5, in 2004. Java's Security Manager could once refuse connections, but
  it was deprecated in 2021, and switched off in 2025. Ruby and Python have tools that refuse network calls in tests,
  but Java had no maintained one. And Spring's profiles arrived with Spring 3.1, in 2011.

## closing: Find out more
- https://github.com/fiftiesHousewife/under-test-airgap — the airgap agent: what it refuses, its report and its design
- https://docs.spring.io/spring-boot/reference/features/profiles.html — Spring Boot's profiles
- https://wiremock.org/docs/proxying/ — WireMock's proxying, for the recording plan
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: give the payment gateway a stand-in; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the airgap agent's own page, Spring Boot's guide to profiles, and
  WireMock's guide to proxying. The links on screen also take you to this lesson's test code, to run yourself. And try
  the exercise in the README: give the payment gateway a stand-in.
