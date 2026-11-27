package io.github.fiftieshousewife.undertest.lessons.video;

/*
 * ffmpeg, which compresses each clip the voice reads for a deck, and which only the machine that voices the decks runs.
 * The system property {@code video.ffmpeg} can name another program, which tests use to put a stand-in in its place.
 */
final class Ffmpeg {

    private Ffmpeg() {
    }

    static String program() {
        return System.getProperty("video.ffmpeg", "ffmpeg");
    }
}
