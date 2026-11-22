package io.github.fiftieshousewife.undertest.techniques.recorded.today;

import io.github.fiftieshousewife.undertest.techniques.recorded.RecordingCleaner;

/** TODAY: saves each recording exactly as recorded, personal data and passwords included. */
public final class TodaysRecordingCleaner implements RecordingCleaner {

    @Override
    public String clean(final String recording) {
        return recording;
    }
}
