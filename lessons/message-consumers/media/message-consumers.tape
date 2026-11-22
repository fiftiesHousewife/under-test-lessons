# Terminal video for the message consumers lesson. Make it from the repository root with:
#   ./gradlew :production:video-render:renderLessonVideo -Plesson=message-consumers
# VHS: https://github.com/charmbracelet/vhs (MIT licence). Videos are regenerated from this script, never edited by hand.
# Follows the make-a-lesson-video skill: one idea, short, only code, build files and real results on screen.

Output lessons/message-consumers/media/message-consumers.gif
Output lessons/message-consumers/media/message-consumers.silent.mp4

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

Env LESSON "message-consumers"
Source lessons/_shared/setup.tape

# ---- Scene 1: step 1, the version catalogue ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 1: add Apache Artemis, a message broker, and the JMS API to the version catalogue."`
Enter
Sleep 300ms
Type@1ms `show libs.versions.toml "/artemis|jakarta-jms/" gradle/libs.versions.toml`
Enter
Sleep 300ms
Show
Sleep 16s

# ---- Scene 2: step 2, the dependencies ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Step 2: the shop's code needs only the JMS API; the tests get the whole broker."`
Enter
Sleep 300ms
Type@1ms `show lessons/message-consumers/example/build.gradle.kts "/^dependencies/" "lesson build file" --zoom 10-12`
Enter
Sleep 300ms
Show
Sleep 17s

# ---- Scene 3: the example messages ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Example messages, *browsed* from a test environment's queue, then *cleaned*."`
Enter
Sleep 300ms
Type@1ms `note "One, from an old till, gives pounds instead of pence: no code can ever read it."`
Enter
Sleep 300ms
Type@1ms `show lessons/message-consumers/example/src/test/resources/example-messages/payments.txt 1 7 "example messages" --zoom 6`
Enter
Sleep 300ms
Show
Sleep 36s

# ---- Scene 4: the code under test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The code under test: today's consumer. It *acknowledges* each message as it is received."`
Enter
Sleep 300ms
Type@1ms `show TodaysPaymentConsumer 30 50 "today's consumer" --zoom 33-38`
Enter
Sleep 300ms
Show
Sleep 24s

# ---- Scene 5: the test with a hand-built message ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A unit test with a *hand-built* message: one good payment, and no broker."`
Enter
Sleep 300ms
Type@1ms `show PaymentByHandTest 20 28 "the test with a hand-built message" --zoom 22-25`
Enter
Sleep 300ms
Show
Sleep 27s

# ---- Scene 6: a real broker in the test ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "A *real* broker, started inside each test: no network, nothing on disk."`
Enter
Sleep 300ms
Type@1ms `note "Set up as production's: 3 tries, then the dead-letter queue."`
Enter
Sleep 300ms
Type@1ms `show EmbeddedBroker 34 47 "the broker in the test" --zoom 41-43`
Enter
Sleep 300ms
Show
Sleep 26s

# ---- Scene 7: the tests on the broker ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The tests on the broker: the example messages in, then what the consumer did."`
Enter
Sleep 300ms
Type@1ms `show PaymentsOnABrokerTest 19 38 "the tests on the broker" --zoom 21-24`
Enter
Sleep 300ms
Show
Sleep 31s

# ---- Scene 8: the broker finds lost payments ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "Run the tests against today's consumer:"`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:message-consumers:test -Pcode=today"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /tests failed\. Report/
Sleep 18s
Hide
Type@1ms `callout "The hand-built test passes. On the broker, a payment is lost and the unreadable message vanishes."`
Enter
Sleep 300ms
Show
Sleep 6s

# ---- Scene 9: the fixed consumer ----
Hide
Type@1ms "scene"
Enter
Sleep 300ms
Type@1ms `note "The consumer with a *transacted* session: commit after recording, roll back on failure:"`
Enter
Sleep 300ms
Type@1ms `show FixedPaymentConsumer 30 45 "the fixed consumer" --zoom 37-41`
Enter
Sleep 300ms
Type@1ms `run_live "./gradlew :lessons:message-consumers:test"`
Enter
Sleep 300ms
Show
Wait+Screen@300s /All \d+ tests passed/
Sleep 16s
Hide
Type@1ms `callout "Every payment recorded, the bad message kept for people to look at, and all in seconds."`
Enter
Sleep 300ms
Show
Sleep 5s
