# Narration: embedded Jakarta EE tests

One item per scene of [embedded-jakarta-ee-tests.tape](embedded-jakarta-ee-tests.tape). The intro and closing slides
have their own narration, in [embedded-jakarta-ee-tests.slides.md](embedded-jakarta-ee-tests.slides.md). Written to be
spoken.

1. Step one. Add Weld's JUnit support to the version catalogue, with the programming interface for CDI. CDI is short for
   Contexts and Dependency Injection: the part of Jakarta EE that wires objects together.
2. Step two. The lesson compiles against the CDI interface, which the application server provides in production. And
   its tests use Weld.
3. First, the mark itself. It's a CDI qualifier: an annotation that lets code ask for a bean by name. The standard
   rate carries the mark, and gives 20%. The reduced rate gives 5%, and has no mark at all.
4. Here is the code under test, today's checkout. It asks the container for a tax rate, without saying which. The
   reduced rate came along, and the checkout never changed.
5. This is a unit test that builds the checkout by hand, and hands it the standard rate itself. So it
   checks the sums, but never gets the rate the server chooses.
6. Next, a test in a container. Weld's JUnit extension starts Weld for each test. Weld is the container application
   servers use, and here it runs inside the test, with no server to deploy to. The test lists the parts it needs: the
   checkout and both rates. Then it asks Weld for the checkout, wired as the server would wire it, and checks that £10
   comes to £12.
7. We run the tests against today's checkout. Built by hand, the checkout is right. In the container, the checkout gets
   the reduced rate, because it's the only one without a mark. So £10 comes to £10.50, instead of £12.
8. Now the checkout that asks for the standard rate, by name: its constructor marks the rate it wants as standard. We
   run the same tests. [pause 1s] It's wired as intended.
