# Terminal video for the embedded Jakarta EE tests lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=embedded-jakarta-ee-tests
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/embedded-jakarta-ee-tests/media/embedded-jakarta-ee-tests.gif
Output lessons/embedded-jakarta-ee-tests/media/embedded-jakarta-ee-tests.silent.mp4

Require java
Require git

Set Shell "bash"
Set FontSize 20
Set Width 1920
Set Height 1080
Set Padding 24
Set Theme "Builtin Dark"
Set TypingSpeed 40ms
Set WindowBar Colorful
Set CursorBlink false

Env LESSON "embedded-jakarta-ee-tests"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Weld's JUnit support and the CDI API to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/weld|cdi/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 2: step 2, the dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the CDI API to compile against (the server provides it), and Weld for the tests."`
Enter
Sleep 300ms
Type@1ms `show lessons/embedded-jakarta-ee-tests/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10-12`
Enter
Sleep 300ms
Show
Sleep 13s

# ---- Scene 3: the two rates ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Two tax rates: the *standard* rate, marked @Standard, and a *reduced* rate added later, with no mark."`
Enter
Sleep 300ms
Type@1ms `show StandardRate 5 13 "the standard rate" --zoom 7`
Enter
Sleep 300ms
Type@1ms `show ReducedRate 5 12 "the reduced rate"`
Enter
Sleep 300ms
Show
Sleep 15s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's checkout. It asks the container for *a* tax rate."`
Enter
Sleep 300ms
Type@1ms `show TodaysCheckout 9 22 "today's checkout" --zoom 14-15`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 5: the test by hand ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A unit test builds the checkout *by hand*, and hands it the standard rate itself."`
Enter
Sleep 300ms
Type@1ms `show CheckoutByHandTest 10 13 "the test by hand" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 6: the test in a container ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A test in a *container*: Weld starts inside the test, and wires the beans as the server would."`
Enter
Sleep 300ms
Type@1ms `show CheckoutInContainerTest 15 24 "the test in a container" --zoom 18-19`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 7: the container finds the wiring bug ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's checkout:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:embedded-jakarta-ee-tests:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 13s
Hide
Type@1ms `callout "By hand, it's right. In the container, the checkout gets the reduced rate: 5% instead of 20%."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 8: the fixed checkout ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The checkout that asks for the standard rate by name:"`
Enter
Sleep 300ms
Type@1ms `show StandardCheckout 15 18 "the fixed checkout" --zoom 16`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:embedded-jakarta-ee-tests:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 8s
Hide
Type@1ms `callout "Wired as intended, and tested in seconds, with no application server."`
Enter
Sleep 300ms
Show
Sleep 5s
