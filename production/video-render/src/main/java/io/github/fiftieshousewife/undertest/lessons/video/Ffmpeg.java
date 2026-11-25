package io.github.fiftieshousewife.undertest.lessons.video;

import java.nio.file.Path;
import java.util.List;
import java.util.OptionalDouble;

/*
 * ffmpeg and ffprobe, which only the machine that makes the videos runs. Their names can be changed with the system
 * properties {@code video.ffmpeg} and {@code video.ffprobe}, which tests use to put stand-ins in their place.
 */
final class Ffmpeg {

    private Ffmpeg() {
    }

    static String program() {
        return System.getProperty("video.ffmpeg", "ffmpeg");
    }

    static String probe() {
        return System.getProperty("video.ffprobe", "ffprobe");
    }

    /* A video's length in seconds, measured with ffprobe, or empty if the file does not exist. */
    static OptionalDouble videoSeconds(final Path video) {
        if (!video.toFile().isFile()) {
            return OptionalDouble.empty();
        }
        final String output = ExternalCommand.run(List.of(probe(), "-v", "error", "-show_entries", "format=duration",
                "-of", "csv=p=0", video.toAbsolutePath().toString()), Path.of("").toAbsolutePath());
        return OptionalDouble.of(Double.parseDouble(output.strip()));
    }
}
