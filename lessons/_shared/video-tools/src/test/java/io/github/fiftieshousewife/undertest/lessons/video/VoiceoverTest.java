package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class VoiceoverTest {

    private static final WavFile ONE_SECOND = new WavFile(100, 1, new byte[200]);
    private static final WavFile THREE_SECONDS = new WavFile(100, 1, new byte[600]);

    @Test
    void reportsEachSceneWhoseNarrationDoesNotFit() {
        final List<Scene> scenes = List.of(new Scene("why", 0, 5), new Scene("build", 5, 8));

        assertAll(
                () -> assertThat(Voiceover.misfits(scenes, List.of(ONE_SECOND, ONE_SECOND))).isEmpty(),
                () -> assertThat(Voiceover.misfits(scenes, List.of(ONE_SECOND, THREE_SECONDS)))
                        .containsExactly("scene 2 (build): narration needs 4.0 s but the scene shows for 3.0 s;"
                                + " add 1 s to its pauses"));
    }

    @Test
    void placesEachClipAtTheStartOfItsScene() {
        final List<Scene> scenes = List.of(new Scene("a", 0, 2), new Scene("b", 2, 6));
        final WavFile track = Voiceover.track(scenes, List.of(ONE_SECOND, ONE_SECOND));

        assertAll(
                () -> assertThat(track.seconds()).isEqualTo(6),
                () -> assertThat(track.sampleRate()).isEqualTo(100));
    }
}
