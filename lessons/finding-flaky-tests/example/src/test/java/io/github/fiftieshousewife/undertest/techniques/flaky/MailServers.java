package io.github.fiftieshousewife.undertest.techniques.flaky;

import io.github.fiftieshousewife.undertest.techniques.flaky.fixed.QuietMailServer;
import io.github.fiftieshousewife.undertest.techniques.flaky.today.TodaysBusyMailServer;
import java.util.Set;

/**
 * The mail server for the tests: on today's busy machine (-Pcode=today, and -Pcode=answer, which runs the exercise's
 * answer beside today's tests), or on a quiet one.
 */
public final class MailServers {

    private MailServers() {
    }

    public static MailServer chosen() {
        return Set.of("today", "answer").contains(System.getProperty("code"))
                ? new TodaysBusyMailServer() : new QuietMailServer();
    }
}
