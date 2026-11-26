# Narration: comparing structured output

One item per scene of [structured-output-comparison.tape](structured-output-comparison.tape). The intro and closing
slides have their own narration, in [structured-output-comparison.slides.md](structured-output-comparison.slides.md).
Written to be spoken.

1. Step one. Add JSONUnit to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. JSONUnit reads JSON with Jackson, which the code already uses.
3. The code writes an order summary as JSON, for the warehouse. It has a new id, and the time it was made, so those
   two fields are different on every run.
4. Here is the code under test, today's total. It adds up each line's price, and forgets how many were bought. So 4
   teas at 2 pounds 50, and 1 packet of biscuits at 1 pound 20, come to 3 pounds 70, not 11 pounds 20.
5. This is a loosened test. Comparing the whole text broke on every run, because of the id and the time.
   So we loosened it, and now it only checks that some words are there, whatever the numbers are.
6. Now, a structured comparison, with JSONUnit. The test hands it the summary, and the JSON it expects. It checks every
   field and every value. The order of the fields and the spacing don't matter. In the expected JSON, the id and the
   time are placeholders that accept any text, so they only have to be there.
7. We run both tests against today's total. The loosened test passes. The structured comparison fails, and names the
   field and both values: the total in pence should be 1120, but was 370.
8. Now the total that multiplies price by quantity. We run the same tests. [pause 1s] Both pass, on every run.
