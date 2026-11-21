# Shared, hidden set-up for every lesson video. Include it with:  Source lessons/_shared/setup.tape
# It never appears on camera: it fetches Gradle, resolves every plugin and library, compiles the lesson, and gives the
# terminal a short prompt and the callout helper, so the video starts on a ready, warmed-up project.
#
# It waits for a marker printed when the set-up finishes. The marker is computed ($((1+1)) prints 2), so the typed
# command, which also appears on screen, can't match it early.
Hide
Type "export PS1='$ ' GREP_COLORS='mt=01;33' VIDEO_TIMINGS=lessons/${LESSON}/media/${LESSON}.timings && source lessons/_shared/callout.sh && : > $VIDEO_TIMINGS"
Enter
Sleep 300ms
# Quiet Gradle on camera without changing the commands people see: quiet logging (test results and build summaries
# still show; warnings, which can contain full paths, do not), no deprecation summary, no configuration-cache notes.
# Recordings also keep their own Gradle background processes (daemon registry), so they never join one started
# elsewhere, for example inside a sandbox where test workers cannot connect.
Type `export GRADLE_OPTS="$GRADLE_OPTS -Dorg.gradle.warning.mode=none -Dorg.gradle.configuration-cache=false -Dorg.gradle.daemon.registry.base=$PWD/build/video-daemons -Dorg.gradle.logging.level=quiet"`
Enter
Sleep 300ms
Type "./gradlew -q --console=plain help >/dev/null 2>&1; ./gradlew -q --console=plain :lessons:${LESSON}:testClasses >/dev/null 2>&1; echo SETUP-$((1+1))-DONE"
Enter
Wait+Screen@600s /SETUP-2-DONE/
Type "clear"
Enter
Sleep 500ms
Show
