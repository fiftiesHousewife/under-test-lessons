package io.github.fiftieshousewife.undertest.lessons.video;

/* A lesson's name as the production tools log it: on one line, so a log line can't be split in two. */
final class LogSafe {

    private LogSafe() {
    }

    static String lesson(final Media media) {
        return media.lesson().replace('\r', ' ').replace('\n', ' ');
    }
}
