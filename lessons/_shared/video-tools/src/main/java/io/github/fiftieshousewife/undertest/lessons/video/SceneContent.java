package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * What one scene of a video script puts on screen, read from the script itself: the code panels it shows (the same
 * lines {@code show} prints), the commands it runs live with the output kept for each ({@link LiveOutputs}), and its
 * callouts. Slide decks draw these as text.
 */
final class SceneContent {

    /* A panel of code: its label, its lines (each with its line number), and the numbers of the lines marked. */
    record Panel(String label, List<String> lines, Set<Integer> marked) {

        Panel {
            lines = List.copyOf(lines);
            marked = Set.copyOf(marked);
        }
    }

    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final List<String> LIVE = List.of("run_live", "run_with_notes");
    private static final Pattern WORD = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private final List<Panel> shownPanels = new ArrayList<>();
    private final List<String> liveCommands = new ArrayList<>();
    private final List<String> calloutTexts = new ArrayList<>();
    private final List<String> noteTexts = new ArrayList<>();
    private final List<List<String>> liveOutputs = new ArrayList<>();

    List<Panel> panels() {
        return List.copyOf(shownPanels);
    }

    List<String> commands() {
        return List.copyOf(liveCommands);
    }

    /* For each live command, in the same order, the output kept for it (empty if none). */
    List<List<String>> outputs() {
        return List.copyOf(liveOutputs);
    }

    List<String> callouts() {
        return List.copyOf(calloutTexts);
    }

    /* The scene's captions, as the video shows them at the top of the screen. */
    List<String> notes() {
        return List.copyOf(noteTexts);
    }

    /* Reads every scene of a script, in order; paths in {@code show} lines are relative to the repository root. */
    static List<SceneContent> read(final Path root, final Path tape) {
        final List<Path> places = ShownCode.places(root, tape);
        final List<SceneContent> scenes = new ArrayList<>();
        SceneContent current = null;
        for (final String line : lines(tape)) {
            if (line.matches("^#\\s*-+\\s*Scene\\s+\\d+:.*")) {
                current = new SceneContent();
                scenes.add(current);
            } else if (current != null) {
                current.take(root, places, line);
            }
        }
        readOutputs(root, tape, scenes);
        return scenes;
    }

    private static void readOutputs(final Path root, final Path tape, final List<SceneContent> scenes) {
        final String name = String.valueOf(tape.getFileName());
        final Media media = new Media(root, name.endsWith(".tape") ? name.substring(0, name.length() - 5) : name);
        int index = 0;
        for (final SceneContent scene : scenes) {
            for (final String command : scene.liveCommands) {
                scene.liveOutputs.add(LiveOutputs.read(media, index++, command));
            }
        }
    }

    private void take(final Path root, final List<Path> places, final String line) {
        final Matcher typed = TYPED.matcher(line);
        if (typed.matches()) {
            take(root, places, words(typed.group(2)));
        }
    }

    private void take(final Path root, final List<Path> places, final List<String> words) {
        if (LIVE.contains(words.getFirst())) {
            liveCommands.add(words.get(1));
            return;
        }
        switch (words.getFirst()) {
            case "show" -> shownPanels.add(ShownCode.panel(root, places, words.subList(1, words.size())));
            case "callout" -> calloutTexts.add(words.get(1));
            case "note" -> noteTexts.add(words.get(1));
            default -> {
                // other commands are set-up
            }
        }
    }

    static List<String> words(final String text) {
        final List<String> words = new ArrayList<>();
        final Matcher word = WORD.matcher(text);
        while (word.find()) {
            words.add(word.group(1) != null ? word.group(1) : word.group(2));
        }
        return words;
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
