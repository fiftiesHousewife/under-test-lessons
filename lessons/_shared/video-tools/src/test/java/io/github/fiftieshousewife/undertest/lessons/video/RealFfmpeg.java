package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/** For tests that run the real ffmpeg on generated videos, where it is installed. */
final class RealFfmpeg {

    private RealFfmpeg() {
    }

    /** True if ffmpeg and ffprobe are both on the PATH. */
    static boolean installed() {
        return onPath("ffmpeg") && onPath("ffprobe");
    }

    /** One value ffprobe reports for a video's first video stream, such as {@code stream=width,height}. */
    static String probe(final Path folder, final Path video, final String entries) {
        return ExternalCommand.run(List.of("ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries",
                entries, "-of", "csv=p=0", video.toString()), folder).strip().split("\n")[0];
    }

    private static boolean onPath(final String program) {
        return Arrays.stream(System.getenv().getOrDefault("PATH", "").split(File.pathSeparator))
                .anyMatch(folder -> Files.isExecutable(Path.of(folder, program)));
    }
}
