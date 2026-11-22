# A short recording that checks the zoom on key code works in VHS's terminal before any lesson is recorded:
#   ./gradlew :lessons:video-tools:checkZoomInRecording
# It shows one code panel with --zoom 22-24, as lessons do, so the helper reports where those lines landed.
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
Output build/zoom-check/zoom-check.silent.mp4

Hide
Type "export PS1='$ ' VIDEO_ZOOMS=build/zoom-check/zoom-check.zooms LESSON=mutation-testing && source lessons/_shared/callout.sh && : > $VIDEO_ZOOMS && clear"
Enter
Sleep 1s
# ---- Scene 1: a code panel, zoomed ----
Type@1ms `show lessons/mutation-testing/example/src/main/java/io/github/fiftieshousewife/undertest/techniques/mutation/DiscountCalculator.java 17 39 --zoom 22-24`
Enter
Sleep 1s
Show
Sleep 7s
