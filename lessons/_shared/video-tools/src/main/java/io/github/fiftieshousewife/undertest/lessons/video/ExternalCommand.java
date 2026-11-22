package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Runs an outside program, such as the installed browser that draws slides, and returns what it printed. The tools
 * that make videos run ffmpeg and the voice through it too ({@code Ffmpeg}).
 */
final class ExternalCommand {

    private static final int TAIL_CHARACTERS = 2000;

    private ExternalCommand() {
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
}
