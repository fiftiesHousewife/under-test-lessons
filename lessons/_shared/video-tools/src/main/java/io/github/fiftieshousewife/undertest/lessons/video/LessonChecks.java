package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/* Checks that catch what reviews found by hand: code panels that would show the wrong file or lines. */
final class LessonChecks {

    private LessonChecks() {
    }

    /* Each {@code show} line finds a file (in its lesson first) and any line range fits inside it. */
    static List<Problem> shows(final Path root, final Path sceneFile) {
        final List<Path> places = ShownCode.places(root, sceneFile);
        final List<Problem> problems = new ArrayList<>();
        for (final List<String> words : SceneContent.commandLines(sceneFile)) {
            if (words.size() > 1 && "show".equals(words.getFirst())) {
                problems.addAll(show(root, places, words));
            }
        }
        return problems.stream().map(message -> new Problem(sceneFile, message.message())).toList();
    }

    private static List<Problem> show(final Path root, final List<Path> places, final List<String> words) {
        final Path file;
        try {
            file = ShownCode.find(root, places, words.get(1));
        } catch (IllegalArgumentException e) {
            return List.of(new Problem(root, e.getMessage()));
        }
        if (asksForLines(words)) {
            final int length = lines(file).size();
            if (Integer.parseInt(words.get(3)) > length) {
                return List.of(new Problem(root, "show " + words.get(1) + " asks for lines up to " + words.get(3)
                        + ", but " + file.getFileName() + " has " + length));
            }
        }
        return List.of();
    }

    private static boolean asksForLines(final List<String> words) {
        return words.size() > 3 && words.get(2).matches("\\d+") && words.get(3).matches("\\d+");
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
