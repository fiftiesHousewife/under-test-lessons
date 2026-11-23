# Slides: embedded Jakarta EE tests

Sources: weld.cdi-spec.org (Weld, the reference implementation of CDI, Apache 2.0 licence, used in WildFly and other
servers); github.com/weld/weld-testing (weld-junit5); jakarta.ee (CDI, first released as part of Java EE 6 in 2009).

## intro: The checkout charges 5% VAT, not 20%
- On a £10 item, the checkout should charge £12: 20% VAT
- Since a reduced rate of 5% was added, the server wires in that one, and charges £10.50
- The unit test passes: it builds the checkout by hand, and hands it the rate it wants
Picture: bug
Say: Here is the problem. On a £10 item, the checkout should charge £12, with VAT at 20%. But since a reduced rate of
  5% was added, the server gives the checkout that rate instead, and it charges £10.50. Nobody changed the checkout's
  code. And the unit test passes, because it builds the checkout by hand, and hands it the rate it wants. It never
  sees what the server chooses.

## intro: Our example: VAT at the checkout
- The checkout adds VAT to a net price
- The standard rate is 20%, marked as standard
- A reduced rate of 5% was added later, with no mark
Picture: tag
Say: Our example is a checkout that adds VAT to a net price. The standard rate is 20%, and is marked as standard. A
  reduced rate of 5% was added later, with no mark.

## intro: The fix: let the real wiring build the checkout
- Old enterprise Java runs in an application server, whose container wires the parts together
- An embedded test starts just that container, inside the test, in seconds
- It wires the checkout exactly as production does, so the test sees the wrong rate
- The fixed checkout asks for the rate marked standard
Picture: server
Say: The fix is to let the real wiring build the checkout. Old enterprise Java runs inside an application server.
  Its container decides what each part gets, and wires them together. The standard for that wiring is called CDI,
  short for contexts and dependency injection. An embedded test starts just that container, inside the test, in
  seconds, with no server to deploy to. It wires the checkout exactly as production does, so the test sees the wrong
  rate. And the fixed checkout asks for the rate marked standard.

## intro: How Weld's JUnit support looks
- @ExtendWith(WeldJunit5Extension.class)
- @WeldSetup WeldInitiator.of(checkout, StandardRate.class, ReducedRate.class)
- weld.select(...).get().totalPence(1000), expected 1200
- Today: expected 1200 but was 1050
Picture: robot
Say: Here is how it looks, with Weld, the container many servers use. A JUnit extension starts it for each test. The
  test lists the parts it needs: the checkout and both rates. Weld starts a container with just those. The test asks
  it for the checkout, which arrives fully wired, and works out the total on 1000 pence. Today it expected 1200
  pence, and got 1050.

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
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: add a third rate with no mark, and see what the container does; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Weld site, and its testing project. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: add a zero rate with no mark,
  and check the checkout still charges the standard rate.
