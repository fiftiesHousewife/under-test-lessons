# Narration: characterisation tests

One item per scene of [characterisation-tests.tape](characterisation-tests.tape). The intro and closing slides have their
own narration, in [characterisation-tests.slides.md](characterisation-tests.slides.md). Written to be spoken.

1. Step one. Add ApprovalTests to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. There is nothing to configure.
3. Here is the printer's loop, which has run for years without a real test. Each transaction moves the running
   balance and prints a line. The highlighted rule charges the 25 pound fee when a payment takes the balance to zero or
   below. After the loop, interest goes on a closing balance over 1000.
4. Next, a weak test, the kind old code often has. It prints a short statement, then checks only that a statement came
   back with a closing balance line. It would pass whatever the amounts were.
5. The characterisation test prints a month that reaches every rule: a balance of exactly zero, an overdraft, and
   interest. One line hands the whole statement to ApprovalTests. On the first run, ApprovalTests writes it to a
   received file. Once we've checked it, we rename it to the approved file, which sits beside the test.
6. We run the tests. The statement matches the approved file. Look at the approved statement. After the rent, the
   balance is exactly zero, and the printer charges an overdraft fee. The bank had forgotten that quirk. Now the test
   pins it down.
7. Here is a tidier rewrite of the printer. It reads better, and the weak test would still pass. But its fee rule now
   says below zero, not zero or below. We run the characterisation test against the rewrite. [pause 1s] It fails, and
   shows each line that changed, approved and received side by side. The fee at zero has gone, and the closing balance
   moved from 2030 pounds to 2055. Is that a fix or a bug? That is a question for the application owner.
