package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.ByteArrayOutputStream;

/*
 * A sound with each quiet stretch between sounds cut to at most a number of frames, half kept from each end of it
 * ({@link WavFile#withShorterGaps}). {@code LOUDEST_QUIET_SAMPLE} is about -45 dB below a full-scale 16-bit sample.
 */
final class ShorterGaps {

    private static final int LOUDEST_QUIET_SAMPLE = 184;

    private final WavFile sound;
    private final byte[] samples;
    private final int frameBytes;
    private final int frameCount;
    private final int keep;
    private final ByteArrayOutputStream out;
    private int written;

    private ShorterGaps(final WavFile sound, final int keep) {
        this.sound = sound;
        this.samples = sound.samples();
        this.frameBytes = sound.channels() * WavFile.BYTES_PER_SAMPLE;
        this.frameCount = samples.length / frameBytes;
        this.keep = keep;
        this.out = new ByteArrayOutputStream(samples.length);
    }

    static WavFile of(final WavFile sound, final int keep) {
        final ShorterGaps gaps = new ShorterGaps(sound, keep);
        int frame = 0;
        while (frame < gaps.frameCount) {
            if (gaps.quiet(frame)) {
                final int start = frame;
                frame = gaps.endOfQuiet(start);
                gaps.quietBetween(start, frame);
            } else {
                frame++;
            }
        }
        return gaps.rest();
    }

    private int endOfQuiet(final int start) {
        int frame = start;
        while (frame < frameCount && quiet(frame)) {
            frame++;
        }
        return frame;
    }

    /* Leaves out the middle of a quiet stretch between sounds that is longer than it keeps. */
    private void quietBetween(final int start, final int end) {
        if (start > 0 && end < frameCount && end - start > keep) {
            out.write(samples, written * frameBytes, (start + keep / 2 - written) * frameBytes);
            written = end - (keep - keep / 2);
        }
    }

    private WavFile rest() {
        out.write(samples, written * frameBytes, samples.length - written * frameBytes);
        return new WavFile(sound.sampleRate(), sound.channels(), out.toByteArray());
    }

    private boolean quiet(final int frame) {
        for (int channel = 0; channel < sound.channels(); channel++) {
            final int at = frame * frameBytes + channel * WavFile.BYTES_PER_SAMPLE;
            final int sample = (short) ((samples[at] & 0xff) | (samples[at + 1] << 8));
            if (Math.abs(sample) > LOUDEST_QUIET_SAMPLE) {
                return false;
            }
        }
        return true;
    }
}
