# Narration: characterisation tests

One item per scene of [characterisation-tests.tape](characterisation-tests.tape). The intro and closing slides have their
own narration, in [characterisation-tests.slides.md](characterisation-tests.slides.md). Written to be spoken.

1. Step one. Add ApprovalTests to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. There is nothing to configure.
3. Here is the code under test, a bank statement printer that has run for years without tests. It keeps a running
   balance, charges an overdraft fee, and adds interest on a high balance.
4. Now, [pause 300ms] a weak test, the kind legacy code often has. It checks that something came back, so it would pass whatever the
   amounts were.
5. Now, [pause 300ms] the characterisation test. It prints a month that reaches every rule, and approves the whole statement.
6. We run the tests. The statement matches the approved file. Look at the approved statement. After the rent, the
   balance is exactly zero, and an overdraft fee is charged. That is a quirk nobody remembered, now pinned down.
7. Now someone tidies the printer up. The rewrite reads better, and the old tests would still pass. We run the
   characterisation test against the rewrite. [pause 1s] It fails, and shows exactly what changed. The fee at zero has
   gone, and every balance after it moved. Is that a fix or a bug? That is a question for the application owner.
