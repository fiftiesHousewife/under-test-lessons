# Terminal video for the running old code unplugged lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=running-old-code-unplugged
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/running-old-code-unplugged/media/running-old-code-unplugged.gif
Output lessons/running-old-code-unplugged/media/running-old-code-unplugged.silent.mp4

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

Env LESSON "running-old-code-unplugged"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: the airgap agent and its *JUnit listener*, in the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/airgap/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 14s

# ---- Scene 2: step 2, the agent on the test JVM ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the agent's jar, *named on the test JVM's command line*, with its report and the service's packages."`
Enter
Sleep 300ms
Type@1ms `show lessons/running-old-code-unplugged/example/build.gradle.kts 24 44 "lesson build file" --zoom 42 --mark 28,29`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 3: the service's settings ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The service's settings, as they came to us: every address is *production's*."`
Enter
Sleep 300ms
Type@1ms `show lessons/running-old-code-unplugged/example/src/main/resources/application.yml 1 15 "the service's settings: application.yml" --zoom 6 --mark 10,12`
Enter
Sleep 300ms
Show
Sleep 22s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: a price from the *database*, a delivery charge from the *courier*."`
Enter
Sleep 300ms
Type@1ms `show ProductPrices 18 22 "the shop's database" --mark 19`
Enter
Sleep 300ms
Type@1ms `show DeliveryRates 14 25 "the courier's rates service" --zoom 20`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 5: the service's own tests ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The service's *own* tests: each starts the whole service, with its settings."`
Enter
Sleep 300ms
Type@1ms `show QuotesTest 13 38 "the service's own tests" --zoom 25-28`
Enter
Sleep 300ms
Show
Sleep 20s

# ---- Scene 6: today, every call is refused ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the service's tests under the agent, with only the service's settings:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:running-old-code-unplugged:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 18s
Hide
Type@1ms `callout "Every call to another machine is refused, and the summary names each one's tests, code and setting."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 7: the test profile ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The fix: a *test profile*, with a stand-in for each address the report named."`
Enter
Sleep 300ms
Type@1ms `show lessons/running-old-code-unplugged/example/src/test/resources/application-test.yml 1 11 "the test profile: application-test.yml" --zoom 4 --mark 11`
Enter
Sleep 300ms
Type@1ms `show lessons/running-old-code-unplugged/example/build.gradle.kts 55 58 "lesson build file" --mark 57`
Enter
Sleep 300ms
Show
Sleep 30s

# ---- Scene 8: the courier's stand-in ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The courier's stand-in: *WireMock* on this machine, answering from its mapping files."`
Enter
Sleep 300ms
Type@1ms `show StandIns 15 30 "the courier's stand-in" --zoom 21-22`
Enter
Sleep 300ms
Type@1ms `show lessons/running-old-code-unplugged/example/src/test/resources/stand-ins/rates/mappings/rates-sw1a.json 1 8 "its mapping file"`
Enter
Sleep 300ms
Show
Sleep 21s

# ---- Scene 9: the fix, a clean report ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The same tests, with the test profile:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:running-old-code-unplugged:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 12s
Hide
Type@1ms `callout "No call left the machine. The report is clean, and what it listed starts the service's connection list."`
Enter
Sleep 300ms
Show
Sleep 5s
