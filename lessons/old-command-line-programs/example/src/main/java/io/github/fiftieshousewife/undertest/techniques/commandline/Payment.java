package io.github.fiftieshousewife.undertest.techniques.commandline;

/** One payment from the nightly file: the account it is for, and the amount in pence. */
public record Payment(String account, long pence) {

    /** Reads one line of the file, such as {@code ACC-1001,2500}. */
    public static Payment parse(final String line) {
        final String[] fields = line.split(",", -1);
        if (fields.length != 2) {
            throw new IllegalArgumentException("a payment line has an account and an amount: " + line);
        }
        return new Payment(fields[0].trim(), Long.parseLong(fields[1].trim()));
    }

    /** True if the payment is more than the largest single payment allowed. */
    public boolean isOver(final long limitInPence) {
        return pence > limitInPence;
    }
}
