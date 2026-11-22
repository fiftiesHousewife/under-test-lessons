package io.github.fiftieshousewife.undertest.techniques.batch;

import io.github.fiftieshousewife.undertest.techniques.batch.fixed.BankSettlementJob;
import io.github.fiftieshousewife.undertest.techniques.batch.today.TodaysSettlementJob;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/** The job the tests run (the fixed one, or today's with -Pcode=today), and the files kept with the tests. */
@Configuration(proxyBeanMethods = false)
@Import(Jobs.Chosen.class)
class Jobs {

    static Path knownFile(final String name) {
        try {
            return Path.of(Jobs.class.getResource("/settlement/" + name).toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Picks the job's Spring configuration from the "code" switch. */
    static final class Chosen implements ImportSelector {

        @Override
        public String[] selectImports(final AnnotationMetadata metadata) {
            final Class<?> job = "today".equals(System.getProperty("code"))
                    ? TodaysSettlementJob.class : BankSettlementJob.class;
            return new String[] {job.getName()};
        }
    }
}
