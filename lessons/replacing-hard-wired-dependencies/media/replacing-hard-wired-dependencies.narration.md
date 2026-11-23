# Narration: replacing hard-wired dependencies

One item per scene of [replacing-hard-wired-dependencies.tape](replacing-hard-wired-dependencies.tape). The intro and
closing slides have their own narration, in
[replacing-hard-wired-dependencies.slides.md](replacing-hard-wired-dependencies.slides.md). Written to be spoken.

1. Step one. Add Mockito to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. One setting tells Mockito to make its stand-ins as plain
   subclasses, so it never needs special access to the test's Java process.
3. Here is the code under test, today's invoice. It adds up the lines in pence, and converts the total to euros. It
   gets the exchange rate from a hard-wired singleton, the live rates, which call the bank over the network.
4. Now, [pause 300ms] the test. Mockito makes a stand-in for the exchange rates, which always says one euro seventeen
   to the pound. The test checks the rounding, and that an empty invoice costs nothing.
5. We run the test against today's invoice. Both tests fail. Today's invoice has nowhere to take the stand-in, so it
   calls the live service, which a test machine can't reach.
6. Now the invoice after one small change. The rates are passed in, through the constructor. Production passes the
   live rates. A test passes the stand-in.
7. We run the same test. [pause 1s] Every test passes, in about a second, with no network and no account.
