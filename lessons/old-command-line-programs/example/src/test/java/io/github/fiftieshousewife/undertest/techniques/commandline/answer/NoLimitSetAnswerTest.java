package io.github.fiftieshousewife.undertest.techniques.commandline.answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.github.fiftieshousewife.undertest.techniques.commandline.fixed.PaymentImport;
import io.github.fiftieshousewife.undertest.techniques.commandline.today.TodaysPaymentImport;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.stream.SystemIn;
import uk.org.webcompere.systemstubs.stream.SystemOut;

/**
 * THE EXERCISE'S ANSWER: the strong tests pin the program's two normal endings. This pins a third, a night when
 * nobody set PAYMENT_LIMIT_PENCE: the program fails before it reads a payment, prints nothing, and never reaches its
 * exit. Today's program does the same without reaching System.exit, so {@code -Pcode=answer} can run it against today's
 * program, and the pinned behaviour holds on both.
 */
@ExtendWith(SystemStubsExtension.class)
class NoLimitSetAnswerTest {

    @SystemStub
    private final EnvironmentVariables environment = new EnvironmentVariables();
    @SystemStub
    private final SystemIn input = new SystemIn();
    @SystemStub
    private final SystemOut output = new SystemOut();
    private final List<Integer> exits = new ArrayList<>();
    private final List<String> printed = new ArrayList<>();

    @Test
    void withNoLimitSetTheProgramFailsBeforeImportingAnything() {
        environment.remove("PAYMENT_LIMIT_PENCE");
        input.setInputStream(new ByteArrayInputStream("ACC-1001,2500\n".getBytes(StandardCharsets.UTF_8)));
        assertAll(
                () -> assertThatThrownBy(program()).isInstanceOf(NumberFormatException.class),
                () -> assertThat(output.getText()).as("printed to standard output").isEmpty(),
                () -> assertThat(printed).as("printed").isEmpty(),
                () -> assertThat(exits).as("exit codes").isEmpty());
    }

    /** Today's program, run with -Pcode=answer, or the one given its exit. */
    private ThrowingCallable program() {
        if ("answer".equals(System.getProperty("code"))) {
            return () -> TodaysPaymentImport.main(new String[0]);
        }
        return new PaymentImport(printed::add, exits::add)::run;
    }
}
