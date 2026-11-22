package io.github.fiftieshousewife.undertest.techniques.flaky;

import io.github.fiftieshousewife.undertest.techniques.flaky.fixed.QuietMailServer;
import io.github.fiftieshousewife.undertest.techniques.flaky.today.TodaysBusyMailServer;

/** The mail server for the tests: on today's busy machine (-Pcode=today), or on a quiet one. */
final class MailServers {

    private MailServers() {
    }

    static MailServer chosen() {
        return "today".equals(System.getProperty("code")) ? new TodaysBusyMailServer() : new QuietMailServer();
    }
}
