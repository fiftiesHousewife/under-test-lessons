# Terminal video for the JUnit extensions lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=junit-extensions
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/junit-extensions/media/junit-extensions.gif
Output lessons/junit-extensions/media/junit-extensions.silent.mp4

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

Env LESSON "junit-extensions"
Source lessons/_shared/setup.tape

# ---- Scene 1: the shop's settings ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A line on a shop receipt reads the *shop-wide settings*: prices with VAT or not, and the rate."`
Enter
Sleep 300ms
Type@1ms `show ShopReceipt 4 11 "a shop receipt line" --zoom 7-8`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 2: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's trade invoice. It switches a shop-wide setting off, and leaves it off."`
Enter
Sleep 300ms
Type@1ms `show TodaysTradeInvoice 8 15 "today's trade invoice" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 19s

# ---- Scene 3: set-up by hand ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Tests with *set-up by hand*: the settings set once, before all the tests, and never put back."`
Enter
Sleep 300ms
Type@1ms `show HandSetUpTest 12 32 "tests set up by hand" --zoom 15-19`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 4: the extension ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *JUnit extension*: standard settings before each test, put back after, and a check nothing leaked."`
Enter
Sleep 300ms
Type@1ms `show StandardShopSettings 11 31 "the extension" --zoom 25-31`
Enter
Sleep 300ms
Show
Sleep 23s

# ---- Scene 5: tests that use it ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same tests, with *no set-up* of their own: one line asks JUnit to use the extension."`
Enter
Sleep 300ms
Type@1ms `show GuardedSettingsTest 11 27 "tests that use the extension" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 6: the leak is caught ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run both sets of tests against today's trade invoice:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-extensions:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 18s
Hide
Type@1ms `callout "By hand, the innocent receipt test fails. The extension fails the test that left the setting changed."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the fixed trade invoice ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The trade invoice that writes its own price without VAT, and leaves the settings alone:"`
Enter
Sleep 300ms
Type@1ms `show FixedTradeInvoice 7 13 "the fixed trade invoice" --zoom 11`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:junit-extensions:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 9s
Hide
Type@1ms `callout "Every test passes. The extension sets up and cleans up for every test that uses it."`
Enter
Sleep 300ms
Show
Sleep 5s
