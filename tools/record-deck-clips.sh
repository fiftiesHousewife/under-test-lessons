#!/usr/bin/env bash
# Records each live-run scene of a lesson on its own, as a clip, and rebuilds its deck to play them. Production only:
# it needs VHS, ffmpeg and the voice, on the machine that makes the videos. Only the live runs are recorded, so it takes
# a few minutes, not the lesson's whole video.
#   tools/record-deck-clips.sh anti-patterns-in-existing-tests
set -euo pipefail
lesson="${1:?give the lesson id, for example anti-patterns-in-existing-tests}"
cd "$(dirname "$0")/.."
./gradlew :production:video-render:recordLessonDeckClips -Plesson="$lesson"
./gradlew :production:video-render:buildLessonDeckVoice -Plesson="$lesson"
echo "Clips in lessons/$lesson/media/$lesson.deck-clips/; open lessons/$lesson/media/$lesson.deck.html to see them."
