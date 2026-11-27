# Narration: Tags: fast tests first, slow tests apart

One item per scene of [junit-tags.scenes](junit-tags.scenes). The intro and closing slides have their own narration, in
[junit-tags.slides.md](junit-tags.slides.md). Written to be spoken.

1. Here are 2 of the 4 quick tests. They give the stock check a warehouse made from a map: 3 car seats on the shelf. One
   test asks for 3 and expects a yes. The other asks for 4, and expects a no. Tests like these take a few thousandths of
   a second each.
2. The slow tests start the warehouse's test system before each test, and it takes 2 seconds to start. Then one test
   asks for the last pushchair on the shelf, and the other for a cot the warehouse doesn't stock. In real code, this is
   a database, a server, or a whole application starting up.
3. We run the quick tests with today's build, which has no tags. It runs all 6 tests. The 4 quick ones, and the 2 slow
   warehouse tests too. So every run, after every small change, waits for the warehouse. And when every run is slow,
   people stop running the tests.
4. JUnit's word for a label is a tag. This annotation of our own, called slow, carries JUnit's tag named slow, so the
   name is written once. Put it on the slow test class, and it tags every test in it.
5. In the build file, the quick task now leaves out every test tagged 'slow'. Today's build, which runs every test,
   is still in the file, so you can run both and compare them.
6. A second task runs the same test classes, but only the tests tagged 'slow'. The check task depends on it, so
   continuous integration, which runs check, still runs every slow test. Nothing goes untested.
7. We run the quick task again. [pause 1s] Only the 4 quick tests run, and none of them waits.
8. And now the slow task, as continuous integration would run it. [pause 1s] Only the 2 tests tagged 'slow' run.
