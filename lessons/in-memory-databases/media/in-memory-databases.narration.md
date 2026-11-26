# Narration: when an in-memory database is enough

One item per scene of [in-memory-databases.tape](in-memory-databases.tape). The intro and closing slides have their
own narration, in [in-memory-databases.slides.md](in-memory-databases.slides.md). Written to be spoken.

1. Step one. H2, a SQL database written in Java, is already in the version catalogue. It goes on the test classpath,
   and that is all. There is no database server to install, and no container. So the tests run on any machine, even
   the many company laptops where containers can't run.
2. This is the schema, the file that creates the invoices table. The application applies it to production's PostgreSQL.
   The tests apply the very same file, so they never test a table typed in by hand.
3. Here is the code under test, today's invoice book. Each new invoice gets the highest number so far, plus one. With no
   invoices yet, the highest number is null. Null plus one is still null, and Java reads it as 0. So the first invoice
   is number 0.
4. Now, the old way. Every test shares one test database, in a file on disk. Its table was typed in by hand, and it has
   held 2 invoices since long ago. Tests add rows, and never take them out.
5. This is the weak test. It issues 2 invoices, and checks that the first number is greater than 0, and the second
   follows on. On the shared database, the table is never empty, so no test can ask what happens to the very first
   invoice.
6. Now, a fresh database for each test, held in memory. Each gets its own name, and the settings from H2's documentation
   that bring it closer to PostgreSQL. That same documentation says these settings cover only a small part of the
   differences. So they narrow the gap, and don't close it. The database keeps one connection open, and applies the real
   schema. When it closes that connection, the database and its rows are gone. After the first, each new database takes
   only a few milliseconds.
7. Before each test, JUnit starts a fresh database, and after each test, it closes it and throws it away. So each of
   the strong tests starts with no invoices at all, and can check exact numbers. The shop's first invoice must be
   number 1. And 3 invoices must be numbered 1, 2 and 3.
8. One more check keeps the choice honest. Its list names SQL that only PostgreSQL runs, or whose answer depends on
   the database, such as the order of nulls. The test hands it the main code's folder, and it reads every SQL file
   there, and the text in the Java code. It fails on any match, because code like that belongs on a real database.
9. We run the tests against today's invoice book. The weak tests, on the shared database, pass, as they always have. On
   a fresh database, the first invoice is number 0, and 3 invoices are numbered 0, 1 and 2. The portable SQL check
   passes, so H2 is the right place for these tests.
10. Now the fix. With no invoices yet, the highest number counts as 0, using a standard SQL function that every
    database runs the same way. We run the same tests. [pause 1s] The first invoice is number 1, and every test passes.
