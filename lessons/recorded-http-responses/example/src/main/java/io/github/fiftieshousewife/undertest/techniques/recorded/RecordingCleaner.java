package io.github.fiftieshousewife.undertest.techniques.recorded;

/** Runs on each recording, as WireMock's JSON, before it is saved to a file. */
public interface RecordingCleaner {

    /** The recording as it will be saved. */
    String clean(String recording);
}
