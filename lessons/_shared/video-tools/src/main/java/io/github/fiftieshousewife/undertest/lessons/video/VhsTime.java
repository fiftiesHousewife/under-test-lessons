package io.github.fiftieshousewife.undertest.lessons.video;

/* Times in a VHS script: durations such as {@code 40ms} or {@code 1.5s}, and how long a {@code Type} line types for. */
final class VhsTime {

    private VhsTime() {
    }

    /* Time to type a {@code Type} line's text: its length times its own speed ({@code Type@40ms}) or the default. */
    static double typing(final String line, final double typingSeconds) {
        final int space = line.indexOf(' ');
        final String rest = space < 0 ? "" : line.substring(space + 1).strip();
        if (!quoted(rest)) {
            return 0;
        }
        final String command = space < 0 ? line : line.substring(0, space);
        return (command.startsWith("Type@") ? duration(command.substring("Type@".length())) : typingSeconds)
                * (rest.length() - 2);
    }

    private static boolean quoted(final String text) {
        return text.length() >= 2 && "\"'`".indexOf(text.charAt(0)) >= 0
                && text.charAt(text.length() - 1) == text.charAt(0);
    }

    /* A VHS duration such as {@code 40ms} or {@code 1.5s}, in seconds. */
    static double duration(final String text) {
        return text.endsWith("ms") ? seconds(text.substring(0, text.length() - 2), "ms")
                : seconds(text.substring(0, text.length() - 1), "s");
    }

    static double seconds(final String amount, final String unit) {
        final double value = Double.parseDouble(amount);
        return "ms".equals(unit) ? value / 1000 : value;
    }
}
