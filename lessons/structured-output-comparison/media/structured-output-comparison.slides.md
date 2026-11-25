# Slides: comparing structured output

Sources: github.com/lukas-krecan/JsonUnit (Apache 2.0 licence, by Lukáš Křečan; AssertJ, Hamcrest and JUnit support in
its README); xmlunit.org (XMLUnit, for Java and .NET).

## intro: Comparing structured output
- The problem: the total is wrong, and the test passes
- The fix: compare the output as data
- The tool: JSONUnit
Picture: road
Say: This lesson is about comparing structured output, such as JSON, as data rather than as text. JSONUnit is a library
  that compares JSON that way. We'll see a wrong total that a loose test lets through. Then JSONUnit names the field,
  and both values.

## intro: The total is wrong, and the test passes
- 4 teas at £2.50 and 1 packet of biscuits at £1.20 should come to £11.20
- Today's summary says £3.70: it forgets how many of each were bought
- The test only checks that some words appear, so it passes
Picture: bug
Say: Here is the problem. An order has 4 teas at 2 pounds 50, and 1 packet of biscuits at 1 pound 20. That should come
  to 11 pounds 20. Today's order summary says 3 pounds 70, because it forgets how many of each were bought. And its test
  passes. It only checks that a few words appear, whatever the numbers are.

## intro: Our example: an order summary
- An order goes to the warehouse as JSON, a common text format for data
- It lists each item, its price, and how many
- It has a total, a new id, and the time it was made
Picture: basket
Say: Our example is an order summary, sent to the warehouse as JSON, a common text format for data. It lists each
  item, its price, and how many were bought. And it has a total, a new id, and the time it was made. The id and the
  time are different on every run. That's why we loosened the test. Comparing the whole text failed every time.

## intro: The fix: compare the output as data
- Check every field and value: the items, the quantities, the total
- Field order and spacing don't matter
- Only the id and the time may vary: a placeholder accepts any text there
Picture: braces
Say: The fix is to compare the summary as data, not as text. This is called a structured comparison. It checks every
  field and every value: the items, the quantities and the total. The order of the fields and the spacing don't
  matter. And only the parts that must vary, the id and the time, get a placeholder that accepts any text.

## intro: How JSONUnit looks
- assertThatJson(summary).isEqualTo(the expected JSON)
- For the id: "${json-unit.any-string}"
- A difference names the field: totalPence, expected 1120 but was 370
Picture: robot
Say: JSONUnit is a library for this. The test hands it the summary, and the JSON it expects. In the expected JSON, the
  id and the time are placeholders that accept any string. When the two differ, it names the field and both values.
  For the shop's order, it says the total in pence should be 1120, but was 370.

Include: how-we-build

## closing: When to compare structure
- Whenever a test checks JSON or XML: answers from web services (API responses), messages, files for other systems
- Ignore only fields that must vary, and check they are present
- For XML, XMLUnit does the same job
Picture: map
Say: When should you use it? [pause 1600ms] Whenever a test checks JSON or XML: API responses, messages, and files for
  other systems. Ignore only the fields that must vary, and still check that they are there. For XML, XMLUnit does the
  same job.

## closing: Where JSONUnit came from
- Written by Lukáš Křečan, free under the Apache licence
- Works with AssertJ, Hamcrest, and plain JUnit
- XMLUnit does the same for XML, in Java and .NET
Picture: book
Say: Lukáš Křečan wrote JSONUnit, and it's free under the Apache licence. It works with AssertJ, with Hamcrest,
  and with plain JUnit. XMLUnit does the same for XML, in both Java and .NET.

## closing: Find out more
- https://github.com/lukas-krecan/JsonUnit — JSONUnit, with examples
- https://www.xmlunit.org — XMLUnit, for XML
- {lessons-repo} — run this lesson yourself: its test code and examples
- Exercise: stop the clock, and check the time exactly; the README has the steps and the answer
Picture: globe
Say: To find out more, start with JSONUnit's page, full of examples, and XMLUnit's site for XML. The links on screen
  also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: stop
  the clock, and check the time exactly, so only the id needs a placeholder.
