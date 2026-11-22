# Slides: embedded Jakarta EE tests

Sources: weld.cdi-spec.org (Weld, the reference implementation of CDI, Apache 2.0 licence, used in WildFly and other
servers); github.com/weld/weld-testing (weld-junit5); jakarta.ee (CDI, first released as part of Java EE 6 in 2009).

## intro: What is an embedded Jakarta EE test?
- Old enterprise Java runs inside an application server, which wires its parts together
- An embedded test starts just the piece it needs, such as the CDI container, inside the test
- The parts are wired exactly as in production, without deploying anything
Picture: server
Say: Old enterprise Java runs inside an application server, which wires its parts together. An embedded test starts
  just the piece it needs, such as the container that does the wiring, inside the test itself. The parts are wired
  exactly as they are in production, without deploying anything.

## intro: Our example: VAT at the checkout
- The checkout adds VAT to a net price
- The standard rate is 20%, marked as standard
- A reduced rate of 5% was added later, with no mark
Picture: tag
Say: Our example is a checkout that adds VAT to a net price. The standard rate is twenty percent, and is marked as
  standard. A reduced rate of five percent was added later, with no mark.

## intro: What we'll compare
- The code under test: today's checkout, which asks for "a tax rate"
- A unit test: builds the checkout by hand, with the rate it chooses
- A test in a container: Weld wires the checkout as the server does
Picture: scales
Say: Here is what we will compare. The code under test is today's checkout, which asks the container for a tax rate.
  [pause 300ms] A unit test builds the checkout by hand, with the rate it chooses. [pause 300ms] And a test in a
  container lets Weld wire the checkout, as the server does.

## intro: Why test the wiring
- In enterprise Java, the container decides what each part gets
- A new class can change that, without touching the code that uses it
- Tests on a real server are slow, so they're rare; embedded tests take seconds
Picture: magnifier
Say: Why test the wiring? [pause 1600ms] In enterprise Java, the container decides what each part gets. Adding a new
  class can change that, without touching the code that uses it. And tests on a real server are slow, so teams run
  them rarely. Embedded tests take seconds.

## intro: How Weld's JUnit support works
- List the beans the test needs; Weld starts a container with just those
- Ask the container for a bean, and it arrives fully wired
- The container stops when the test ends
Picture: robot
Say: With Weld's JUnit support, you list the beans the test needs, and Weld starts a container with just those. Ask
  the container for a bean, and it arrives fully wired. When the test ends, the container stops.

Include: how-we-build

## closing: When to use embedded tests
- For old Java EE and Jakarta EE code: CDI beans, and EJBs with Weld's extra support
- Before adding a bean that could change what others get
- Keep server-level tests for what truly needs the server: security, transactions across systems
Picture: map
Say: When should you use them? [pause 1600ms] For old Java E E and Jakarta E E code: CDI beans, and E J Bs too, with
  Weld's extra support. Use them before adding any bean that could change what others get. And keep the slow server
  tests for what truly needs a server.

## closing: Where Weld came from
- CDI arrived with Java EE 6, in 2009, and lives on in Jakarta EE
- Weld is its reference implementation, inside WildFly and other servers
- weld-junit brings it into JUnit tests
Picture: book
Say: CDI arrived with Java E E six, in 2009, and lives on in Jakarta E E. Weld is its reference implementation, the
  one inside WildFly and other servers. And the Weld team's JUnit support brings it into ordinary tests.

## closing: Find out more
- https://weld.cdi-spec.org — Weld
- https://github.com/weld/weld-testing — Weld's JUnit support
- {lessons-repo} — this lesson, to run yourself
Picture: globe
Say: To find out more, start with the Weld site, and its testing project. The links on screen also take you to this
  lesson's code.
