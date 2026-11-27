package io.github.fiftieshousewife.undertest.techniques.fuzzing;

/**
 * Reads one line of the courier's label file: a name, a postcode and a weight in grams, separated by semicolons, such
 * as {@code Ada Brennan;SW1A 1AA;500}. The contract: it returns the label, or refuses a line it can't read with an
 * {@link IllegalArgumentException} saying why. Any other exception is a bug.
 */
public interface LabelReader {

    /** The label on this line; an {@link IllegalArgumentException} if the line isn't a label. */
    Label read(String line);
}
