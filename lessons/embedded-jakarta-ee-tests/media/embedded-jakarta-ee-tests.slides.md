# Slides: embedded Jakarta EE tests

Sources: weld.cdi-spec.org (Weld, the reference implementation of CDI, Apache 2.0 licence, used in WildFly and other
servers); github.com/weld/weld-testing (weld-junit5); jakarta.ee (CDI, first released as part of Java EE 6 in 2009).

## intro: Embedded Jakarta EE tests
- The problem: the checkout charges 5% VAT, not 20%
- The fix: let the real wiring build it
- The tool: Weld, with its JUnit support
Picture: road
Say: Let's look at testing Jakarta EE code with its real wiring, in a small container started inside the test. Weld is
  the part of Jakarta EE that wires an application's parts together. We'll see a checkout that charges the wrong VAT.
  Then Weld's JUnit support builds the checkout as the server would.

## intro: Our example: VAT at the checkout
- The checkout adds VAT to a net price
- The standard rate is 20%, marked as standard
- A reduced rate of 5%, added later, with no mark
Picture: tag
Say: Our example is a checkout that adds VAT to a net price. The standard rate is 20%, marked as standard. That mark
  arrived with a reduced rate of 5%. The reduced rate has no mark.

Include: how-we-build

## closing: When to use embedded tests
- For old Java EE and Jakarta EE code: CDI beans, and EJBs with Weld's extra support
- Before adding a bean that could change which bean other code is given
- Keep server-level tests for what truly needs the server: security, transactions across systems
Picture: map
Say: When should you use embedded tests? [pause 1600ms] For old Java E E and Jakarta E E code: CDI beans, and E J Bs
  too, with Weld's extra support. Use them before adding any bean that could change what others get. And keep the slow
  server tests for what truly needs a server.

## closing: Where Weld came from
- CDI arrived with Java EE 6, in 2009, and lives on in Jakarta EE
- Weld is its reference implementation, inside WildFly and other servers
- weld-junit brings it into JUnit tests
Picture: book
Say: CDI arrived with Java E E six, in 2009, and lives on in Jakarta E E. Weld is its reference implementation, the one
  inside WildFly and other servers. And the Weld team's JUnit support brings it into ordinary tests.

## closing: Find out more
- https://weld.cdi-spec.org — Weld
- https://github.com/weld/weld-testing — Weld's JUnit support
- {lessons-repo}/tree/main/lessons/embedded-jakarta-ee-tests — run this lesson yourself: its test code and examples
- Exercise: add a third rate with no mark, and see what the container does; the README has the steps and the answer
Picture: globe
Say: To find out more, start with the Weld site, and its testing project. The links on screen also take you to this
  lesson's test code and examples, to run yourself. And try the exercise in the README: add a zero rate with no mark,
  and check the checkout still charges the standard rate.
