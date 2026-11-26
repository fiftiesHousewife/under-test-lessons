# Slides: comparing structured output

Sources: github.com/lukas-krecan/JsonUnit (Apache 2.0 licence, by Lukáš Křečan; AssertJ, Hamcrest and JUnit support in
its README); xmlunit.org (XMLUnit, for Java and .NET).

## intro: Comparing structured output
- The problem: the total is wrong, and the test passes
- The fix: compare the output as data
- The tool: JSONUnit
Picture: road
Say: Here, we look at comparing structured output, such as JSON, as data rather than as text. JSONUnit is a library that
  compares JSON field by field. We'll see a wrong total that a loose test lets through. Then JSONUnit names the field,
  and both values.

## intro: Our example: an order summary
- An order goes to the warehouse as JSON, a common text format for data
- It lists each item, its price, and how many
- It has a total, a new id, and the time it was made
Picture: basket
Say: Our example is an order summary, sent to the warehouse as JSON, a common text format for data. It lists each item,
  its price, and how many were bought. And it has a total, a new id, and the time it was made.

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
Say: Lukáš Křečan wrote JSONUnit, and it's free under the Apache licence. It works with AssertJ, with Hamcrest, and with
  plain JUnit. XMLUnit does the same for XML, in both Java and .NET.

## closing: Find out more
- https://github.com/lukas-krecan/JsonUnit — JSONUnit, with examples
- https://www.xmlunit.org — XMLUnit, for XML
- {lessons-repo}/tree/main/lessons/structured-output-comparison — run this lesson yourself: its test code and examples
- Exercise: stop the clock, and check the time exactly; the README has the steps and the answer
Picture: globe
Say: To find out more, start with JSONUnit's page, full of examples, and XMLUnit's site for XML. The links on screen
  also take you to this lesson's test code and examples, to run yourself. And try the exercise in the README: stop the
  clock, and check the time exactly, so only the id needs a placeholder.
