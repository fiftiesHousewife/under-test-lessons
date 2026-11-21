# Narration: comparing structured output

One item per scene of [structured-output-comparison.tape](structured-output-comparison.tape). The intro and closing
slides have their own narration, in [structured-output-comparison.slides.md](structured-output-comparison.slides.md).
Written to be spoken.

1. Step one. Add JSONUnit to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. JSONUnit reads JSON with Jackson, which the code already uses.
3. The code writes an order summary as JSON, for the warehouse. It has a new id, and the time it was made, so those
   two fields are different on every run.
4. Here is the code under test, today's total. It adds up each line's price, and forgets how many were bought.
5. This is [pause 300ms] a loosened test. Comparing the whole text broke on every run, because of the id and the time.
   So someone loosened it, and now it only checks that some words are there.
6. Now, [pause 300ms] a structured comparison, with JSONUnit. It checks every field and every value, in any order. The
   id and the time only have to be there, as text.
7. We run both tests against today's total. The loosened test passes. The structured comparison fails, and says
   exactly what is wrong: the total, what it expected, and what it found.
8. Now the total that multiplies price by quantity. We run the same tests. [pause 1s] Both pass, on every run.
