package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalDouble;

/**
 * Runs the outside programs the video tools use: the voice command, {@code ffprobe} and {@code ffmpeg}. Their names can
 * be changed with the system properties {@code video.ffmpeg} and {@code video.ffprobe}, which tests use to put
 * stand-ins in their place.
 */
final class ExternalCommand {

    private static final int TAIL_CHARACTERS = 2000;

    private ExternalCommand() {
    }

    static String ffmpeg() {
        return System.getProperty("video.ffmpeg", "ffmpeg");
    }

    static String ffprobe() {
        return System.getProperty("video.ffprobe", "ffprobe");
    }

    /** Runs a command in a folder and returns everything it printed; fails with the end of its output if it fails. */
    @SuppressFBWarnings(value = "COMMAND_INJECTION",
            justification = "Runs the maintainer's own configured tools on files these tools name themselves")
    static String run(final List<String> command, final Path folder) {
        try {
            final Process process = new ProcessBuilder(command).directory(folder.toFile()).redirectErrorStream(true)
                    .start();
            final String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            final int exit = process.waitFor();
            if (exit != 0) {
                throw new IllegalStateException(command.getFirst() + " failed with exit code " + exit + ":\n"
                        + output.substring(Math.max(0, output.length() - TAIL_CHARACTERS)));
            }
            return output;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while running " + command.getFirst(), e);
        }
    }

    /** A video's length in seconds, measured with ffprobe, or empty if the file does not exist. */
    static OptionalDouble videoSeconds(final Path video) {
        if (!video.toFile().isFile()) {
            return OptionalDouble.empty();
        }
        final String output = run(List.of(ffprobe(), "-v", "error", "-show_entries", "format=duration",
                "-of", "csv=p=0", video.toAbsolutePath().toString()), Path.of("").toAbsolutePath());
        return OptionalDouble.of(Double.parseDouble(output.strip()));
    }
}
