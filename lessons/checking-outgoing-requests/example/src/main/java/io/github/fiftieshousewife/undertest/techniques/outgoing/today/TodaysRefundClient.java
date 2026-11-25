package io.github.fiftieshousewife.undertest.techniques.outgoing.today;

import io.github.fiftieshousewife.undertest.techniques.outgoing.ProviderHttp;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refund;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refunds;
import java.net.http.HttpRequest;
import tools.jackson.databind.node.ObjectNode;

/**
 * TODAY: sends the amount in pounds, where the provider expects pence, and no Idempotency-Key header, so a refund sent
 * twice is paid twice. The provider still answers 201 Created.
 */
public final class TodaysRefundClient implements Refunds {

    private static final int OLD_MINIMUM_PENCE = 50; // TODAY, too: a minimum the provider dropped long ago

    private final ProviderHttp provider;

    public TodaysRefundClient(final ProviderHttp provider) {
        this.provider = provider;
    }

    @Override
    public String send(final Refund refund) {
        if (refund.pence() < OLD_MINIMUM_PENCE) {
            return "not sent: under the minimum";
        }
        final ObjectNode body = ProviderHttp.newBody()
                .put("orderId", refund.orderId())
                .put("amount", refund.pence() / 100.0)
                .put("currency", "GBP");
        return provider.send(provider.refundRequest()
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build());
    }
}
