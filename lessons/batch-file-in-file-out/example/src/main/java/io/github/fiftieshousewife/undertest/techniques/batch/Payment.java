package io.github.fiftieshousewife.undertest.techniques.batch;

/** One payment from the day's input file: who paid, and how much, in pence. */
public record Payment(String account, int pence) {

    /** Reads a line such as {@code 12345678,1250}. */
    public static Payment parse(final String line) {
        final String[] fields = line.split(",");
        return new Payment(fields[0].strip(), Integer.parseInt(fields[1].strip()));
    }

    /** The payment as a line of the settlement file: {@code PAY,12345678,12.50}. */
    public String settlementLine() {
        return "PAY,%s,%d.%02d".formatted(account, pence / 100, pence % 100);
    }
}
