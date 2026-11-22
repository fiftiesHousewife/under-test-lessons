# Narration: embedded Jakarta EE tests

One item per scene of [embedded-jakarta-ee-tests.tape](embedded-jakarta-ee-tests.tape). The intro and closing slides
have their own narration, in [embedded-jakarta-ee-tests.slides.md](embedded-jakarta-ee-tests.slides.md). Written to be
spoken.

1. Step one. Add Weld's JUnit support, and the CDI programming interface, to the version catalogue.
2. Step two. The lesson compiles against the CDI interface, which the application server provides in production. And
   its tests use Weld.
3. Here are two tax rates. The standard rate, twenty percent, is marked standard. A reduced rate, five percent, was
   added later, with no mark at all.
4. Here is the code under test, today's checkout. It asks the container for a tax rate, without saying which.
5. This is [pause 300ms] a unit test that builds the checkout by hand, and hands it the standard rate itself. So it
   checks the sums, but not the wiring.
6. Now, [pause 300ms] a test in a container. Weld, the same container application servers use, starts inside the
   test, and wires the beans exactly as the server would.
7. We run the tests against today's checkout. By hand, it's right. In the container, the checkout gets the reduced
   rate, because it's the only one without a mark. Five percent, instead of twenty.
8. Now the checkout that asks for the standard rate, by name. We run the same tests. [pause 1s] It's wired as
   intended.
