package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.ArrayList;
import java.util.List;

/** Captions for the whole spliced video: intro slides, then the recording's scenes, then closing slides. */
final class SpliceCaptions {

    private SpliceCaptions() {
    }

    static List<Cue> cues(final Media media, final Slides slides, final Splicer.Timings timings) {
        final List<Cue> cues = new ArrayList<>(slideCues(slides.intro(), timings.intro(), 0));
        final double introSeconds = timings.introSeconds();
        final List<Scene> scenes = media.scenes();
        for (final Cue cue : CaptionFile.cues(scenes, Narration.paragraphs(media.file(".narration.md")))) {
            cues.add(new Cue(cue.start() + introSeconds, cue.end() + introSeconds, cue.text()));
        }
        cues.addAll(slideCues(slides.closing(), timings.closing(), introSeconds + scenes.getLast().end()));
        return cues;
    }

    /** Each slide's narration as cues across its segment, starting at the given time. */
    static List<Cue> slideCues(final List<Slide> slides, final List<Splicer.Segment> segments, final double start) {
        final List<Cue> cues = new ArrayList<>();
        double time = start;
        for (int i = 0; i < slides.size(); i++) {
            final double end = time + segments.get(i).seconds();
            cues.addAll(CaptionFile.cuesFor(new Scene(slides.get(i).title(), time,
                    end - Splicer.SLIDE_BREATH_SECONDS), slides.get(i).narration()));
            time = end;
        }
        return cues;
    }
}
