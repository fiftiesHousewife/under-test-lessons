package io.github.fiftieshousewife.undertest.techniques.capture;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.util.Map;
import tools.jackson.databind.json.JsonMapper;

/**
 * The old service: GET /customers/{id}, with a caller's token, answers the customer's email, tier and loyalty points,
 * which it asks the loyalty provider for.
 */
@SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Made in code, never serialised")
public final class CustomerServlet extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final JsonMapper JSON = JsonMapper.builder().build();

    private final transient Map<String, Customer> customers;
    private final transient LoyaltyClient loyalty;

    public CustomerServlet(final Map<String, Customer> customers, final LoyaltyClient loyalty) {
        super();
        this.customers = Map.copyOf(customers);
        this.loyalty = loyalty;
    }

    @Override
    @SuppressFBWarnings(value = {"SERVLET_HEADER", "XSS_SERVLET"},
            justification = "The example only checks that a token is there; the answer is JSON that Jackson writes")
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String token = request.getHeader("Authorization");
        final Customer customer = customers.get(String.valueOf(request.getPathInfo()).replaceFirst("^/", ""));
        if (token == null || !token.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        } else if (customer == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setContentType("application/json");
            response.getWriter().write(JSON.createObjectNode()
                    .put("id", customer.id())
                    .put("email", customer.email())
                    .put("tier", customer.tier())
                    .put("points", loyalty.pointsFor(customer.email()))
                    .toString());
        }
    }
}
