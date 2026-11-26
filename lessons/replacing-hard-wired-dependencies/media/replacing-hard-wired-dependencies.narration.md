# Narration: replacing hard-wired dependencies

One item per scene of [replacing-hard-wired-dependencies.tape](replacing-hard-wired-dependencies.tape). The intro and
closing slides have their own narration, in
[replacing-hard-wired-dependencies.slides.md](replacing-hard-wired-dependencies.slides.md). Written to be spoken.

1. Step one. Add Mockito to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. Only the tests use it.
3. Here is the code under test, today's invoice. It adds up the lines in pence, and converts the total to euros. It
   gets the exchange rate from a singleton, the one and only live rate service, which calls the bank over the network.
   When code reaches for something, instead of being given it, that's a hard-wired dependency, and a test can't swap it.
4. Now, the test. Mockito makes a stand-in for the exchange rates. Before each test, we tell it what to answer: one
   euro seventeen to the pound. Then the test hands it to the invoice. £12.50 comes to 1463 euro cents, rounding up
   the half cent. And an empty invoice costs nothing.
5. We run the test against today's invoice. Both tests fail with the same error, before they check a single number.
   Today's invoice has nowhere to take the stand-in, so it calls the live service. A test machine has no network, and
   no account for the bank.
6. Now the invoice after one small change. It takes its rates through its constructor, and keeps them. Handing a class
   what it needs, instead of letting it fetch it, is called dependency injection. The invoice no longer chooses its
   rates. Whatever builds it does: the live rates, or a stand-in.
7. We run the same test. [pause 1s] Every test passes, in about a second, with no network and no account.
