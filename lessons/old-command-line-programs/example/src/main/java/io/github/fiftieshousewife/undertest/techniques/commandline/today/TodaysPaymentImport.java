package io.github.fiftieshousewife.undertest.techniques.commandline.today;

import io.github.fiftieshousewife.undertest.techniques.commandline.Payment;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/** TODAY: an old program. It reads an environment variable and standard input, prints, and calls System.exit. */
public final class TodaysPaymentImport {

    private TodaysPaymentImport() {
    }

    @SuppressForbidden // System.out: allowed in today's code only, so the lesson shows old code as it really is
    public static void main(final String[] args) {
        final long limit = Long.parseLong(System.getenv("PAYMENT_LIMIT_PAISE"));
        final BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        final List<String> lines = input.lines().toList();
        long total = 0;
        for (final String line : lines) {
            final Payment payment = Payment.parse(line);
            if (payment.isOver(limit)) {
                System.out.println("over the limit: " + payment.account());
                System.exit(1);
            }
            System.out.println("accepted " + payment.account() + " " + payment.paise());
            total += payment.paise();
        }
        System.out.println(lines.size() + " payments, " + total + " paise");
    }
}
