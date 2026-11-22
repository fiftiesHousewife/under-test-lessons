package io.github.fiftieshousewife.undertest.techniques.messages;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.JMSProducer;
import jakarta.jms.Queue;
import java.util.List;
import org.apache.activemq.artemis.api.core.QueueConfiguration;
import org.apache.activemq.artemis.api.core.RoutingType;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.embedded.EmbeddedActiveMQ;
import org.apache.activemq.artemis.core.settings.impl.AddressSettings;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * A real Artemis broker, started inside the test for each test and stopped after it. It is reached in the same JVM
 * (no network), keeps nothing on disk, and is set up as production's broker is: three tries, then the dead-letter
 * queue.
 */
final class EmbeddedBroker implements BeforeEachCallback, AfterEachCallback {

    static final String DEAD_LETTERS = "payments.dead-letter";
    private static final String ADDRESS = "vm://0";

    private final EmbeddedActiveMQ broker = new EmbeddedActiveMQ();
    private final ActiveMQConnectionFactory connections = new ActiveMQConnectionFactory(ADDRESS);

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final Configuration settings = new ConfigurationImpl()
                .setPersistenceEnabled(false)
                .setSecurityEnabled(false)
                .setJMXManagementEnabled(false)
                .addAcceptorConfiguration("in-vm", ADDRESS)
                .addAddressSetting("#", new AddressSettings()
                        .setMaxDeliveryAttempts(3)
                        .setDeadLetterAddress(SimpleString.of(DEAD_LETTERS)))
                .addQueueConfiguration(QueueConfiguration.of(PaymentConsumer.QUEUE).setRoutingType(RoutingType.ANYCAST))
                .addQueueConfiguration(QueueConfiguration.of(DEAD_LETTERS).setRoutingType(RoutingType.ANYCAST));
        broker.setConfiguration(settings).start();
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        connections.close();
        broker.stop();
    }

    ConnectionFactory factory() {
        return connections;
    }

    /** Sends each text as a message to the payments queue. */
    void sendPayments(final List<String> texts) {
        try (JMSContext jms = connections.createContext()) {
            final Queue payments = jms.createQueue(PaymentConsumer.QUEUE);
            final JMSProducer producer = jms.createProducer();
            texts.forEach(text -> producer.send(payments, text));
        }
    }

    /** The text of every message on a queue, read with a queue browser, so none is taken off it. */
    List<String> browse(final String queue) throws JMSException {
        return QueueBrowsing.textOn(connections, queue);
    }
}
