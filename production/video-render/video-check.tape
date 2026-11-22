# A short recording for the video check, before any lesson is recorded:
#   ./gradlew :production:video-render:checkVideoInRecording
# One code panel rolls in with --zoom 22-24, as in lessons, so the helper reports where those lines landed, then a
# callout slides in.
Require bash
Set Shell "bash"
Set FontSize 20
Set Width 1920
Set Height 1080
Set Padding 24
Set Theme "Builtin Dark"
Set TypingSpeed 40ms
Set WindowBar Colorful
Set CursorBlink false
Output build/video-check/video-check.silent.mp4

Hide
Type "export PS1='$ ' VIDEO_ROLL=1 VIDEO_ZOOMS=build/video-check/video-check.zooms LESSON=mutation-testing && source lessons/_shared/callout.sh && : > $VIDEO_ZOOMS && clear"
Enter
Sleep 1s
# ---- Scene 1: a code panel, zoomed ----
Type@1ms `show lessons/mutation-testing/example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mutation/DiscountCalculator.java 17 39 --zoom 22-24`
Enter
Sleep 1s
Show
Sleep 7s
Hide
Type@1ms `callout "Only mutation testing shows which tests catch bugs."`
Enter
Show
Sleep 3s
