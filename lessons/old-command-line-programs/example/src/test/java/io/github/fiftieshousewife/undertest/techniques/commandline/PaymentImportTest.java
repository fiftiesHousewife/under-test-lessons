package io.github.fiftieshousewife.undertest.techniques.commandline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemIn;
import uk.org.webcompere.systemstubs.stream.SystemOut;

/*
 * AFTER: runs the whole program, with its environment variable, input and output stubbed by System Stubs. The exit code
 * stands in for System.exit: -1 until the program ends.
 */
@ExtendWith(SystemStubsExtension.class)
class PaymentImportTest {

    @SystemStub
    private final EnvironmentVariables environment = new EnvironmentVariables("PAYMENT_LIMIT_PENCE", "50000");
    @SystemStub
    private final SystemIn input = new SystemIn();
    @SystemStub
    private final SystemOut output = new SystemOut();
    private final AtomicInteger exitCode = new AtomicInteger(-1);

    @Test
    void acceptsPaymentsUpToTheLimitAndEndsWithZero() throws Throwable {
        input.setInputStream(Lines.of("ACC-1001,2500", "ACC-1002,50000"));
        Programs.paymentImport(output, exitCode::set).execute();
        assertAll(
            () -> assertThat(output.getLines()).containsExactly(
                "accepted ACC-1001 2500", "accepted ACC-1002 50000", "2 payments, 52500 pence"),
            () -> assertThat(exitCode).as("exit code").hasValue(0));
    }

    @Test
    void stopsAtThePaymentOverTheLimitAndEndsWithOne() throws Throwable {
        environment.set("PAYMENT_LIMIT_PENCE", "2000");
        input.setInputStream(Lines.of("ACC-1001,1500", "ACC-1002,2500", "ACC-1003,1000"));
        Programs.paymentImport(output, exitCode::set).execute();
        assertAll(
            () -> assertThat(output.getLines()).containsExactly("accepted ACC-1001 1500", "over the limit: ACC-1002"),
            () -> assertThat(exitCode).as("exit code").hasValue(1));
    }
}
