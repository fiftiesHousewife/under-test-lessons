package io.github.fiftieshousewife.undertest.techniques.messages;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/** Reads a payment from a message such as {@code order=A-1001;customer=customer-7f3a;pence=2599}. */
public final class PaymentMessages {

    private PaymentMessages() {
    }

    public static Payment read(final Message message) throws JMSException {
        if (!(message instanceof TextMessage text) || text.getText() == null) {
            throw new PaymentNotRecorded("not a text message");
        }
        final Map<String, String> fields = new HashMap<>();
        for (final String field : text.getText().split(";")) {
            final String[] pair = field.split("=", 2);
            fields.put(pair[0].trim(), pair.length == 2 ? pair[1].trim() : "");
        }
        final String pence = fields.getOrDefault("pence", "");
        if (!fields.containsKey("order") || !pence.matches("\\d{1,9}")) {
            throw new PaymentNotRecorded("no order, or the amount is not a whole number of pence");
        }
        return new Payment(fields.get("order"), Integer.parseInt(pence));
    }
}
