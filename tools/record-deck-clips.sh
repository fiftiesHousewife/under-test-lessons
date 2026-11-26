#!/usr/bin/env bash
# Records each live-run scene of a lesson on its own, as a clip, and rebuilds its deck to play them. Production only:
# it needs VHS, ffmpeg and the voice, on the machine that makes the videos. Only the live runs are recorded, so a lesson
# takes a few minutes, not its whole video. "all" does every lesson with a live run, one after another, and carries on
# past a lesson that fails, naming the failures at the end.
#   tools/record-deck-clips.sh anti-patterns-in-existing-tests
#   tools/record-deck-clips.sh all
set -uo pipefail
cd "$(dirname "$0")/.."
which="${1:?give a lesson id, or all}"
if [ "$which" = "all" ]; then
  lessons=$(grep -l "run_live" lessons/*/media/*.tape | sed -E 's|lessons/([^/]+)/media/.*|\1|')
else
  lessons="$which"
fi
# Every script is checked once, here, rather than before each lesson.
./gradlew :lessons:video-tools:checkVideoScripts || exit 1
failed=""
for lesson in $lessons; do
  echo "== $lesson"
  if ./gradlew :production:video-render:recordLessonDeckClips -Plesson="$lesson" -x :lessons:video-tools:checkVideoScripts \
      && ./gradlew :production:video-render:buildLessonDeckVoice -Plesson="$lesson"; then
    echo "   clips in lessons/$lesson/media/$lesson.deck-clips/"
  else
    failed="$failed $lesson"
  fi
done
if [ -n "$failed" ]; then
  echo "These lessons failed:$failed"
  exit 1
fi
echo "Done. Open a lesson's media/<lesson>.deck.html to see its clips."
