package io.github.fiftieshousewife.undertest.techniques.outgoing.fixed;

import io.github.fiftieshousewife.undertest.techniques.outgoing.ProviderHttp;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refund;
import io.github.fiftieshousewife.undertest.techniques.outgoing.Refunds;
import java.net.http.HttpRequest;
import tools.jackson.databind.node.ObjectNode;

/** FIXED: sends the amount in pence, and an Idempotency-Key header, so the provider pays a repeated refund once. */
public final class RefundClient implements Refunds {

    private final ProviderHttp provider;

    public RefundClient(final ProviderHttp provider) {
        this.provider = provider;
    }

    @Override
    public String send(final Refund refund) {
        final ObjectNode body = ProviderHttp.newBody()
                .put("orderId", refund.orderId())
                .put("amount", refund.pence())
                .put("currency", "GBP");
        return provider.send(provider.refundRequest()
                .header("Content-Type", "application/json")
                .header("Idempotency-Key", "refund-" + refund.orderId())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build());
    }
}
