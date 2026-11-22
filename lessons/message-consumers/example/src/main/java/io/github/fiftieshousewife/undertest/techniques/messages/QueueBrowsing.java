package io.github.fiftieshousewife.undertest.techniques.messages;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.QueueBrowser;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Reads the text of every message on a queue without taking any of them off it, with a JMS queue browser. This is how
 * the lesson's example messages were collected from a test environment's queue, before they were cleaned.
 */
public final class QueueBrowsing {

    private QueueBrowsing() {
    }

    public static List<String> textOn(final ConnectionFactory factory, final String queue) throws JMSException {
        final List<String> texts = new ArrayList<>();
        try (Connection connection = factory.createConnection();
                Session session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
                QueueBrowser browser = session.createBrowser(session.createQueue(queue))) {
            connection.start();
            final Enumeration<?> messages = browser.getEnumeration();
            while (messages.hasMoreElements()) {
                if (messages.nextElement() instanceof TextMessage text) {
                    texts.add(text.getText());
                }
            }
        }
        return texts;
    }
}
