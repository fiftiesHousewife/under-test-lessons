package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;

/** A 16-bit PCM WAV sound: enough to measure speech clips, join them with silence, and write the result. */
public record WavFile(int sampleRate, int channels, byte[] samples) {

    static final int BYTES_PER_SAMPLE = 2;
    private static final int HEADER_BYTES = 44;
    private static final int PCM = 1;

    /** Keeps its own copy of the sound, so callers can't change it afterwards. */
    public WavFile {
        samples = samples.clone();
    }

    /** A copy of the sound data. */
    @Override
    public byte[] samples() {
        return samples.clone();
    }

    public static WavFile read(final Path file) {
        final ByteBuffer data = ByteBuffer.wrap(bytes(file)).order(ByteOrder.LITTLE_ENDIAN);
        requireWav(file, data);
        int rate = 0;
        int channels = 0;
        int position = 12;
        while (position + 8 <= data.limit()) {
            final int id = data.getInt(position);
            final int size = data.getInt(position + 4);
            if (id == tag("fmt ")) {
                requirePcm16(file, data, position + 8);
                channels = data.getShort(position + 10);
                rate = data.getInt(position + 12);
            } else if (id == tag("data")) {
                return new WavFile(rate, channels, samples(data, position, size));
            }
            position += 8 + size + (size & 1);
        }
        throw new IllegalArgumentException(file + " has no sound data");
    }

    /** Silence of the given length, in the same format. */
    public WavFile silence(final double seconds) {
        return new WavFile(sampleRate, channels, new byte[frames(seconds) * channels * BYTES_PER_SAMPLE]);
    }

    /**
     * The sound with each quiet stretch between sounds cut to at most the given length, half kept from each end of
     * it; quiet at the start and end is left as it is. Quiet is below about -45 dB, as in a voice's pause between
     * sentences.
     */
    public WavFile withShorterGaps(final double longest) {
        return ShorterGaps.of(this, frames(longest));
    }

    public double seconds() {
        return (double) samples.length / (sampleRate * channels * BYTES_PER_SAMPLE);
    }

    int frames(final double seconds) {
        return (int) Math.round(Math.max(0, seconds) * sampleRate);
    }

    public void write(final Path file) {
        final ByteBuffer out = ByteBuffer.allocate(HEADER_BYTES + samples.length).order(ByteOrder.LITTLE_ENDIAN);
        out.putInt(tag("RIFF")).putInt(36 + samples.length).putInt(tag("WAVE"))
                .putInt(tag("fmt ")).putInt(16).putShort((short) PCM).putShort((short) channels).putInt(sampleRate)
                .putInt(sampleRate * channels * BYTES_PER_SAMPLE).putShort((short) (channels * BYTES_PER_SAMPLE))
                .putShort((short) (BYTES_PER_SAMPLE * 8))
                .putInt(tag("data")).putInt(samples.length).put(samples);
        try {
            Files.write(file, out.array());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /* The sound data of the chunk at {@code position}, as much of it as the file holds. */
    private static byte[] samples(final ByteBuffer data, final int position, final int size) {
        final byte[] samples = new byte[Math.min(size, data.limit() - position - 8)];
        data.get(position + 8, samples);
        return samples;
    }

    private static void requireWav(final Path file, final ByteBuffer data) {
        if (data.remaining() < 12 || data.getInt(0) != tag("RIFF") || data.getInt(8) != tag("WAVE")) {
            throw new IllegalArgumentException(file + " is not a WAV file");
        }
    }

    private static void requirePcm16(final Path file, final ByteBuffer data, final int at) {
        if (data.getShort(at) != PCM || data.getShort(at + 14) != BYTES_PER_SAMPLE * 8) {
            throw new IllegalArgumentException(file + " must be 16-bit PCM");
        }
    }

    private static int tag(final String name) {
        return ByteBuffer.wrap(name.getBytes(java.nio.charset.StandardCharsets.US_ASCII))
                .order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static byte[] bytes(final Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
