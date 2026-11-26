# Narration: real databases in tests

One item per scene of [real-databases-in-tests.tape](real-databases-in-tests.tape). The intro and closing slides have
their own narration, in [real-databases-in-tests.slides.md](real-databases-in-tests.slides.md). Written to be spoken.

1. Step one. Add Testcontainers, the PostgreSQL driver, and the H2 database, to the version catalogue.
2. Step two. Tests that need a container get a suite of their own, called integration test. It adds Testcontainers,
   its JUnit support, and the PostgreSQL driver. Ordinary tests stay quick, and machines with no container runtime skip
   only the container tests.
3. Here is the code under test, today's query. It sorts by the date of the last order, oldest first. It never says
   where a missing date goes. The comment above it warns that databases differ on that.
4. This is a test on H2, a database held in memory. It's quick, and needs nothing installed. It adds 3 customers.
   Bob has never ordered, so the test expects Bob first, then Cy, then Ada. But H2 isn't the database production uses.
5. Now, the same test on real PostgreSQL, the database production uses. A container is a packaged, private copy of a
   program, so each run gets a fresh, empty database. Real databases used to mean shared test servers, slow and
   fragile. Containers make them quick. A label on the class skips the test where there is no container runtime.
   Another marks the container, named by its image, here a small build of PostgreSQL 17. Testcontainers starts it on a
   free port before the test, and removes it afterwards. The test asks it for the address, user and password.
6. We run both suites against today's query. On H2, it passes. On PostgreSQL, it fails. H2 puts customers who never
   ordered first. PostgreSQL sorts a missing date after every real one, so Bob comes last. The sales team would never
   call him.
7. Now the query that says where customers who never ordered belong: nulls first. We run both suites again. [pause 1s]
   Both databases agree.
