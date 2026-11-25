package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * A voice clip's length in seconds, read from its header, so no player or ffmpeg is needed: a WAV clip's data size
 * over its bytes a second, or an M4A clip's MP4 header ({@code moov/mvhd}). {@code WHOLE_FILE} is where {@link #box}
 * looks when it looks at the file's top-level boxes, not inside one.
 */
final class ClipLength {

    private static final int WHOLE_FILE = -1;

    private ClipLength() {
    }

    /* A clip's length in seconds; -1 for an MP3, which has no length in its header. */
    static double seconds(final Path clip) {
        final String name = String.valueOf(clip.getFileName());
        if (name.endsWith(".wav")) {
            return wav(clip);
        }
        return name.endsWith(".m4a") ? mp4(clip) : -1;
    }

    static double wav(final Path clip) {
        final ByteBuffer data = ByteBuffer.wrap(bytes(clip)).order(ByteOrder.LITTLE_ENDIAN);
        int at = 12;
        long bytesPerSecond = 0;
        while (at + 8 <= data.limit()) {
            final String id = fourLetters(data, at);
            final long size = Integer.toUnsignedLong(data.getInt(at + 4));
            if ("fmt ".equals(id)) {
                bytesPerSecond = Integer.toUnsignedLong(data.getInt(at + 16));
            } else if (isSound(id, bytesPerSecond)) {
                return (double) Math.min(size, data.limit() - at - 8) / bytesPerSecond;
            }
            at += (int) (8 + size + (size & 1));
        }
        throw new IllegalArgumentException(clip + " is not a WAV sound file: it has no fmt and data header");
    }

    /* The data chunk, once the format chunk has said how many bytes a second it holds. */
    private static boolean isSound(final String id, final long bytesPerSecond) {
        return "data".equals(id) && bytesPerSecond > 0;
    }

    static double mp4(final Path clip) {
        final ByteBuffer data = ByteBuffer.wrap(bytes(clip));
        final int moov = box(data, WHOLE_FILE, "moov");
        final int mvhd = moov < 0 ? -1 : box(data, moov, "mvhd");
        if (mvhd < 0) {
            throw new IllegalArgumentException(clip + " is not an MP4 sound file: it has no moov/mvhd header");
        }
        return movieSeconds(data, mvhd + 8);
    }

    /* The length an {@code mvhd} box gives: its duration over its timescale, in 32 or 64 bits by its version. */
    private static double movieSeconds(final ByteBuffer data, final int at) {
        final boolean wide = data.get(at) == 1;
        final long timescale = Integer.toUnsignedLong(data.getInt(at + (wide ? 20 : 12)));
        final long duration = wide ? data.getLong(at + 24) : Integer.toUnsignedLong(data.getInt(at + 16));
        return (double) duration / timescale;
    }

    /* Where the box of this type starts, among those inside the box at {@code parent} or the file's, or -1. */
    private static int box(final ByteBuffer data, final int parent, final String type) {
        final int end = contentEnd(data, parent);
        int at = parent == WHOLE_FILE ? 0 : parent + 8;
        while (at + 8 <= end) {
            final long size = size(data, at);
            if (type.equals(fourLetters(data, at + 4))) {
                return at;
            }
            at = size < 8 ? end : at + (int) size;
        }
        return -1;
    }

    private static int contentEnd(final ByteBuffer data, final int parent) {
        return parent == WHOLE_FILE ? data.limit() : parent + (int) size(data, parent);
    }

    private static long size(final ByteBuffer data, final int at) {
        final long size = Integer.toUnsignedLong(data.getInt(at));
        if (size == 1) {
            return data.getLong(at + 8);
        }
        return size == 0 ? data.limit() - at : size;
    }

    private static byte[] bytes(final Path clip) {
        try {
            return Files.readAllBytes(clip);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* The four letters at {@code at}: a WAV chunk's name, or an MP4 box's type (four bytes after its start). */
    private static String fourLetters(final ByteBuffer data, final int at) {
        return new String(new byte[] {data.get(at), data.get(at + 1), data.get(at + 2), data.get(at + 3)},
                StandardCharsets.US_ASCII);
    }
}
