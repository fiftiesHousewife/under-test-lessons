# Narration: old command-line programs

One item per scene of [old-command-line-programs.tape](old-command-line-programs.tape). The intro and closing slides
have their own narration, in [old-command-line-programs.slides.md](old-command-line-programs.slides.md). Written to be
spoken.

1. Step one. Add System Stubs to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. System Stubs' own classes carry SpotBugs annotations, so the
   build needs those too, only to compile.
3. Step three. System Stubs changes the environment variables with a Java agent, from a library called Byte
   Buddy. The build gives that agent to the test process when it starts, so the process never has to attach it to
   itself.
4. Here is the code under test, today's program, all in one main method. It reads its limit straight from the
   environment, and its payments straight from standard input. It prints with Java's own print call. And inside the
   loop, at the payment over the limit, it calls Java's exit, which ends the whole process.
5. Now, the weak tests, all 3 of them. They test the pieces: reading one line, and checking a payment against the
   limit. They never run the program. So they never check its environment variable, its input, what it prints, or how
   it ends.
6. Now the strong test. It runs the whole program, with what it takes from the system swapped for stand-ins. A
   stand-in like this is called a stub. Each stub is a field marked with System Stubs' annotation, and System Stubs puts
   the real one back after every test. One sets the environment variable to 50000 pence, one gives the program its
   standard input, and one catches what it prints. The exit code needs something else. System Stubs used to catch
   Java's exit with the security manager, which Java 24 permanently disabled. So the last field holds the exit code,
   and the test passes the program its own exit, which only records the number.
7. Here is one test. It lowers the limit to 2000 pence, and gives the program three payments. It expects the first to
   be accepted, the second to stop the program, and the exit code to be 1.
8. We run the tests against today's program. [pause 1s] The first test passes: System Stubs runs today's code
   unchanged. Then today's program calls Java's exit, and that stops the whole test run. The other tests never run
   at all.
9. Now the program after one small change. Its constructor takes how to print and how to end. It still reads its
   environment variable and standard input, as before. And it ends in one place, by calling the exit it was given. It
   never calls Java's exit itself. Whatever runs it chooses how it ends, and the test passes an exit that only records
   the code.
10. We run the same tests. [pause 1s] Every test passes, and nothing stops the test run.
