#!/usr/bin/env bash
# Records a lesson's video, cuts its live-run scenes into clips, and rebuilds its deck to play them. Production only:
# it needs VHS, ffmpeg and the voice, on the machine that makes the videos.
#   tools/record-deck-clips.sh anti-patterns-in-existing-tests
set -euo pipefail
lesson="${1:?give the lesson id, for example anti-patterns-in-existing-tests}"
cd "$(dirname "$0")/.."
./gradlew :production:video-render:recordLessonVideo -Plesson="$lesson"
./gradlew :production:video-render:cutLessonDeckClips -Plesson="$lesson"
./gradlew :production:video-render:buildLessonDeckVoice -Plesson="$lesson"
echo "Clips in lessons/$lesson/media/$lesson.deck-clips/; open lessons/$lesson/media/$lesson.deck.html to see them."
