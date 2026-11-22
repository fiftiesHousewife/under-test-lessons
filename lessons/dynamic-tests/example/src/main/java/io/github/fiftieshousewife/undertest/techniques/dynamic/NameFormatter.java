package io.github.fiftieshousewife.undertest.techniques.dynamic;

/** Turns a customer record such as {@code SMITH,JOHN,MR} into the name for a letter: {@code Mr John Smith}. */
public interface NameFormatter {

    String forLetter(String record);

    /** Builds the name from the record's parts, with each part tidied by the given rule. */
    static String assemble(final String record, final java.util.function.UnaryOperator<String> tidy) {
        final String[] parts = record.split(",");
        return tidy.apply(parts[2]) + " " + tidy.apply(parts[1]) + " " + tidy.apply(parts[0]);
    }
}
