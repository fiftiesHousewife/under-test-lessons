package io.github.fiftieshousewife.undertest.techniques.messages;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/** Reads a payment from a message such as {@code payment=P-501;order=A-1001;customer=customer-7f3a;cents=2599}. */
public final class PaymentMessages {

    private PaymentMessages() {
    }

    public static Payment read(final Message message) throws JMSException {
        final Map<String, String> fields = fields(text(message));
        final String cents = fields.getOrDefault("cents", "");
        if (!fields.containsKey("payment") || !fields.containsKey("order") || !cents.matches("\\d{1,9}")) {
            throw new PaymentNotRecorded("no payment id or order, or the amount is not a whole number of cents");
        }
        return new Payment(fields.get("payment"), fields.get("order"), Integer.parseInt(cents));
    }

    private static String text(final Message message) throws JMSException {
        if (!(message instanceof TextMessage text) || text.getText() == null) {
            throw new PaymentNotRecorded("not a text message");
        }
        return text.getText();
    }

    private static Map<String, String> fields(final String text) {
        final Map<String, String> fields = new HashMap<>();
        for (final String field : text.split(";")) {
            final String[] pair = field.split("=", 2);
            fields.put(pair[0].trim(), pair.length == 2 ? pair[1].trim() : "");
        }
        return fields;
    }
}
