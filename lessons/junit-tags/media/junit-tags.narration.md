# Narration: Tags: fast tests first, slow tests apart

One item per scene of [junit-tags.tape](junit-tags.tape). The intro and closing slides have their own narration, in
[junit-tags.slides.md](junit-tags.slides.md). Written to be spoken.

1. Here are quick tests of the stock check. The warehouse is kept in memory, so it answers at once. Tests like these
   take a few thousandths of a second each.
2. And here are [pause 300ms] slow tests. Before each one, they start the warehouse's own test system, which takes 2
   seconds. In real code, this is a database, a server, or a whole application starting up.
3. We run the quick task with today's build, which has no tags. It runs all 6 tests. The 4 quick ones, and the 2 slow
   warehouse tests too. So every run, after every small change, waits for the warehouse.
4. Now, [pause 300ms] a tag. This annotation, called slow, carries JUnit's tag named slow. Put it on the slow test
   class, and every test in it is tagged.
5. In the build file, the quick task now leaves out every test tagged 'slow'. Today's build, which runs every test,
   is still in the file, so you can run both and compare them.
6. A second task runs the same test classes, but only the tests tagged 'slow'. The check task depends on it, so the
   continuous integration, which runs check, still runs every slow test.
7. We run the quick task again. [pause 1s] Only the 4 quick tests run, and none of them waits.
8. And now the slow task, as continuous integration would run it. [pause 1s] Only the 2 tests tagged 'slow' run.
