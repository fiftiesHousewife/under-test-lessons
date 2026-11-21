package io.github.fiftieshousewife.undertest.techniques.recorded.fixed;

import io.github.fiftieshousewife.undertest.techniques.recorded.PostcodeLookup;
import io.github.fiftieshousewife.undertest.techniques.recorded.SupplierHttp;
import io.github.fiftieshousewife.undertest.techniques.recorded.SupplierUnavailableException;
import java.net.http.HttpResponse;
import java.util.Optional;

/** FIXED: checks what the supplier said before reading the answer. */
public final class PostcodeClient implements PostcodeLookup {

    private final SupplierHttp supplier;

    public PostcodeClient(final SupplierHttp supplier) {
        this.supplier = supplier;
    }

    @Override
    public Optional<String> town(final String postcode) {
        final HttpResponse<String> answer = supplier.get(postcode);
        return switch (answer.statusCode()) {
            case 200 -> Optional.of(SupplierHttp.townIn(answer.body()));
            case 404 -> Optional.empty();
            default -> throw new SupplierUnavailableException("the postcode supplier answered " + answer.statusCode());
        };
    }
}
