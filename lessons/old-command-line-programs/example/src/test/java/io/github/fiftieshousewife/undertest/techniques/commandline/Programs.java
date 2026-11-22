package io.github.fiftieshousewife.undertest.techniques.commandline;

import io.github.fiftieshousewife.undertest.techniques.commandline.fixed.PaymentImport;
import io.github.fiftieshousewife.undertest.techniques.commandline.today.TodaysPaymentImport;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.IntConsumer;
import org.junit.jupiter.api.function.Executable;
import uk.org.webcompere.systemstubs.stream.SystemOut;

/** The program the tests run: the one given its exit, or today's with -Pcode=today. */
final class Programs {

    private Programs() {
    }

    /**
     * Today's program has nowhere to take the exit, so it ignores it, and prints to System.out, which System Stubs
     * captures. The fixed one is given the same captured output to print to.
     */
    static Executable paymentImport(final SystemOut output, final IntConsumer exit) {
        if ("today".equals(System.getProperty("code"))) {
            return () -> TodaysPaymentImport.main(new String[0]);
        }
        final PrintStream screen = new PrintStream(output.getOutputStream(), true, StandardCharsets.UTF_8);
        return new PaymentImport(screen::println, exit)::run;
    }
}
