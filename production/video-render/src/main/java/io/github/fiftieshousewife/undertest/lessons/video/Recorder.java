package io.github.fiftieshousewife.undertest.lessons.video;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;

/*
 * Records a lesson's silent video with VHS, reporting brief progress with the elapsed time, and saying when a step is
 * taking a while. On failure it shows the end of VHS's log. The program can be replaced with the system property
 * {@code video.vhs}, which tests use to put a stand-in in its place.
 */
@Slf4j
final class Recorder {

    private static final int LOG_TAIL_LINES = 20;

    private final Clock clock;

    Recorder(final Clock clock) {
        this.clock = clock;
    }

    static String vhs() {
        return System.getProperty("video.vhs", "vhs");
    }

    @SuppressFBWarnings(value = "COMMAND_INJECTION",
            justification = "Runs VHS on the lesson's own script, named by the toolkit itself")
    void record(final Media media) {
        final long start = clock.millis();
        final AtomicLong lastLine = new AtomicLong(start);
        final Deque<String> tail = new ArrayDeque<>();
        final ScheduledExecutorService heartbeat = Executors.newSingleThreadScheduledExecutor();
        heartbeat.scheduleAtFixedRate(() -> Progress.heartbeat((clock.millis() - lastLine.get()) / 1000)
                .ifPresent(message -> say(start, message)), 1, 1, TimeUnit.SECONDS);
        try {
            final Process vhs = new ProcessBuilder(List.of(vhs(), media.file(".tape").toString()))
                    .directory(media.root().toFile()).redirectErrorStream(true).start();
            follow(vhs, new Follow(start, lastLine, tail));
            final int exit = vhs.waitFor();
            if (exit != 0) {
                throw new IllegalStateException("Recording failed. Last lines of the VHS log:\n"
                        + String.join("\n", tail));
            }
            say(start, "done");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while recording", e);
        } finally {
            heartbeat.shutdownNow();
        }
    }

    /* What following VHS's log needs: when recording started, when the last line came, and the last lines seen. */
    private record Follow(long start, AtomicLong lastLine, Deque<String> tail) {
    }

    private void follow(final Process vhs, final Follow follow) throws IOException {
        final Progress progress = new Progress();
        try (BufferedReader lines = new BufferedReader(
                new InputStreamReader(vhs.getInputStream(), StandardCharsets.UTF_8))) {
            for (String line = lines.readLine(); line != null; line = lines.readLine()) {
                follow.lastLine().set(clock.millis());
                follow.tail().addLast(line);
                if (follow.tail().size() > LOG_TAIL_LINES) {
                    follow.tail().removeFirst();
                }
                progress.message(line).ifPresent(message -> say(follow.start(), message));
            }
        }
    }

    private void say(final long start, final String message) {
        log.info("[{}s] {}", String.format("%3d", (clock.millis() - start) / 1000),
                message.replace('\r', ' ').replace('\n', ' '));
    }
}
