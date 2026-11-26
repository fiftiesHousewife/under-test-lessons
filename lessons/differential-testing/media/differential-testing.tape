# Terminal video for the differential testing lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=differential-testing
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/differential-testing/media/differential-testing.gif
Output lessons/differential-testing/media/differential-testing.silent.mp4

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

Env LESSON "differential-testing"
Source lessons/_shared/setup.tape

# ---- Scene 1: the old calculator ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The *old* postage calculator: a chain of ifs, in use for years. Its answers are the prices the business means."`
Enter
Sleep 300ms
Type@1ms `show LegacyPostage 8 23 "the old calculator"`
Enter
Sleep 300ms
Show
Sleep 38s

# ---- Scene 2: the rewrite ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: the *rewrite*. It reads the same prices from a price list."`
Enter
Sleep 300ms
Type@1ms `show RewrittenPostage 17 30 "the rewrite" --zoom 22-23`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 3: the rewrite's own tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The rewrite's *own tests*: one typical weight from each row of the price list."`
Enter
Sleep 300ms
Type@1ms `show RewriteExamplesTest 8 16 "the rewrite's own tests" --zoom 12`
Enter
Sleep 300ms
Show
Sleep 18s

# ---- Scene 4: the differential test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *differential* test: ask both calculators about every weight, and list where they disagree."`
Enter
Sleep 300ms
Type@1ms `show OldAndNewAgreeTest 22 37 "the differential test" --mark 25,37`
Enter
Sleep 300ms
Type@1ms `show OldAndNewAgreeTest 41 59 "every weight in the band, asked of both" --zoom 42-43,58`
Enter
Sleep 300ms
Show
Sleep 50s

# ---- Scene 5: the comparison finds a gap ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against the rewrite as it is today:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:differential-testing:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /completed, \d+ failed/
Sleep 13s
Hide
Type@1ms `callout "Every example passes. The comparison finds 50 weights, 451 g to 500 g, where the rewrite charges more."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 6: the corrected price list ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The price list, checked against the printed one:"`
Enter
Sleep 300ms
Type@1ms `show PriceList 8 12 "the price list" --zoom 9-12`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:differential-testing:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /\d+ tests? passed\./
Sleep 8s
Hide
Type@1ms `callout "The rewrite agrees with the old calculator on all 5,000 weights. Now the old one can go."`
Enter
Sleep 300ms
Show
Sleep 5s
