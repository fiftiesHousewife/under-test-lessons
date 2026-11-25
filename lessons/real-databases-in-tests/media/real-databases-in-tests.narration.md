# Narration: real databases in tests

One item per scene of [real-databases-in-tests.tape](real-databases-in-tests.tape). The intro and closing slides have
their own narration, in [real-databases-in-tests.slides.md](real-databases-in-tests.slides.md). Written to be spoken.

1. Step one. Add Testcontainers, the PostgreSQL driver, and the H2 database, to the version catalogue.
2. Step two. Tests that need a container get a suite of their own, called integration test. Ordinary tests stay quick,
   and machines with no container runtime skip only the container tests.
3. Here is the code under test, today's query. It decides who the sales team should call first: customers who never
   ordered, then those who ordered longest ago. It simply sorts by the date of the last order.
4. This is a test on H2, a database held in memory. It's quick, and needs nothing installed. But it isn't
   the database production uses.
5. Now, the same test on real PostgreSQL, the database production uses. Testcontainers starts it in a
   container for this test, and throws it away afterwards.
6. We run both suites against today's query. On H2, it passes. On PostgreSQL, it fails. H2 puts customers who never
   ordered first. PostgreSQL puts them last. So the sales team would never call them.
7. Now the query that says where customers who never ordered belong: first. We run both suites again. [pause 1s] Both
   databases agree.
