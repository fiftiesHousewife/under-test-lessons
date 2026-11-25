package io.github.fiftieshousewife.undertest.lessons.video;

import java.util.Optional;

/*
 * Turns VHS's step-by-step log into a few readable progress messages: setting up, recording, each scene, PIT runs,
 * callouts and the files being written. Everything else VHS prints is kept only for the error report.
 */
final class Progress {

    private boolean recording;

    /* A message for this log line, if it marks a step worth reporting. */
    Optional<String> message(final String line) {
        return recording ? duringRecording(line) : beforeRecording(line);
    }

    private Optional<String> beforeRecording(final String line) {
        if (line.startsWith("Hide")) {
            return Optional.of("setting up off-camera (Gradle, plugins, compiling)");
        }
        if (line.startsWith("Show")) {
            recording = true;
            return Optional.of("recording");
        }
        return Optional.empty();
    }

    private static Optional<String> duringRecording(final String line) {
        if (line.contains("note ")) {
            return Optional.of("  " + between(line, "note \"", "\""));
        }
        if (line.contains("callout ")) {
            return Optional.of("  callout");
        }
        return runOrFile(line);
    }

    private static Optional<String> runOrFile(final String line) {
        if (line.startsWith("Type") && line.contains("pitest")) {
            return Optional.of("  running PIT");
        }
        if (line.startsWith("Creating ")) {
            return Optional.of("writing " + line.substring("Creating ".length()));
        }
        return Optional.empty();
    }

    private static String between(final String line, final String start, final String end) {
        final int from = line.indexOf(start);
        if (from < 0) {
            return line;
        }
        final int to = line.indexOf(end, from + start.length());
        return line.substring(from + start.length(), to < 0 ? line.length() : to);
    }

    /* What to say when VHS has been quiet: every 20 seconds, how long it has been. */
    static Optional<String> heartbeat(final long quietSeconds) {
        return quietSeconds > 0 && quietSeconds % 20 == 0
                ? Optional.of("    ...still working (" + quietSeconds + "s since the last step)")
                : Optional.empty();
    }
}
