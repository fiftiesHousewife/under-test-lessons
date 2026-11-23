package io.github.fiftieshousewife.undertest.techniques.antipatterns.oldshop;

/** Records each sale for the accountants. */
public interface AuditLog {

    void sale(long orderNumber, long pence);
}
