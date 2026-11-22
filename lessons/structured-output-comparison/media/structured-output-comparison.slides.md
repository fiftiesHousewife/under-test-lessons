# Slides: comparing structured output

Sources: github.com/lukas-krecan/JsonUnit (Apache 2.0 licence, by Lukáš Křečan; AssertJ, Hamcrest and JUnit support in
its README); xmlunit.org (XMLUnit, for Java and .NET).

## intro: What is a structured comparison?
- Compare output as data, not as text: fields, values, lists
- Field order and spacing don't matter
- Say exactly which parts may vary, such as an id or a time
Picture: braces
Say: A structured comparison checks output as data, rather than as text: its fields, its values, and its lists. The
  order of the fields and the spacing don't matter. And you say exactly which parts may vary, such as an id, or the
  time something was made.

## intro: Our example: an order summary
- An order goes to the warehouse as JSON
- It lists each item, its price, and how many
- It has a total, a new id, and the time it was made
Picture: basket
Say: Our example is an order summary, sent to the warehouse as JSON. It lists each item, its price, and how many were
  bought. And it has a total, a new id, and the time it was made.

## intro: What we'll compare
- The code under test: today's total, which forgets the quantities
- A loosened test: checks only that some words appear
- A structured comparison: every field and value, ignoring only what must vary
Picture: scales
Say: Here is what we will compare. The code under test is today's total, which forgets the quantities. [pause 300ms]
  A loosened test checks only that some words appear. [pause 300ms] And a structured comparison checks every field
  and every value, and ignores only what must vary.

## intro: Why text comparisons get loosened
- An id or a time makes the output different on every run
- A whole-text comparison fails every time, so someone loosens it
- A loosened test passes whatever the numbers are
Picture: magnifier
Say: Why does this happen? [pause 1600ms] An id, or a time, makes the output different on every run. A comparison of the
  whole text fails every time, so someone loosens it, to check only a few words. And then it passes, whatever the
  numbers are.

## intro: How JSONUnit works
- Give it the actual JSON and the JSON you expect
- Placeholders such as any-string accept any value of that kind
- A difference names the field, the value expected and the value found
Picture: robot
Say: JSONUnit takes the actual JSON, and the JSON you expect. In the expected JSON, a placeholder such as any string
  accepts any value of that kind. When they differ, it names the field, the value it expected, and the value it found.

Include: how-we-build

## closing: When to compare structure
- Whenever a test checks JSON or XML: API responses, messages, files for other systems
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
Say: JSONUnit was written by Lukáš Křečan, and is free under the Apache licence. It works with AssertJ, with Hamcrest,
  and with plain JUnit. XMLUnit does the same for XML, in both Java and .NET.

## closing: Find out more
- https://github.com/lukas-krecan/JsonUnit — JSONUnit, with examples
- https://www.xmlunit.org — XMLUnit, for XML
- {lessons-repo} — this lesson, to run yourself
Picture: globe
Say: To find out more, JSONUnit's page has plenty of examples, and XMLUnit's site covers XML. The links on screen also
  take you to this lesson's code.
