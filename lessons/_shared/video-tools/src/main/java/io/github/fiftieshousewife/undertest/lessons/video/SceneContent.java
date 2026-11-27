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
 * What one scene of a lesson's scene file ({@code <id>.scenes}) puts on its slide: the code panels it shows, the
 * commands it runs live with the output kept for each ({@link LiveOutputs}), its notes and its callouts. A scene starts
 * at its heading, {@code ## Scene N: title}; each line after it is one command ({@code note}, {@code show},
 * {@code run_live} or {@code run_with_notes}, {@code callout}), its arguments in double quotes where they hold spaces.
 * Blank lines, and other lines starting with {@code #}, are comments.
 */
final class SceneContent {

    /* A panel of code: its label, its lines (each with its line number), and the numbers of the lines marked. */
    record Panel(String label, List<String> lines, Set<Integer> marked) {

        Panel {
            lines = List.copyOf(lines);
            marked = Set.copyOf(marked);
        }
    }

    static final Pattern HEADING = Pattern.compile("^##\\s+Scene\\s+\\d+:\\s*(.*?)\\s*$");
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

    /* The scene's notes, which its slide shows as points ({@link SceneSlide#points}). */
    List<String> notes() {
        return List.copyOf(noteTexts);
    }

    /* Reads every scene of a scene file, in order; paths in {@code show} lines are relative to the repository root. */
    static List<SceneContent> read(final Path root, final Path sceneFile) {
        final List<Path> places = ShownCode.places(root, sceneFile);
        final List<SceneContent> scenes = new ArrayList<>();
        SceneContent current = null;
        for (final String line : lines(sceneFile)) {
            if (HEADING.matcher(line).matches()) {
                current = new SceneContent();
                scenes.add(current);
            } else if (current != null && isCommand(line)) {
                current.take(root, places, words(line.strip()));
            }
        }
        readOutputs(root, sceneFile, scenes);
        return scenes;
    }

    /* Each scene's title, from its heading, in order. */
    static List<String> titles(final Path sceneFile) {
        final List<String> titles = new ArrayList<>();
        for (final String line : lines(sceneFile)) {
            final Matcher heading = HEADING.matcher(line);
            if (heading.matches()) {
                titles.add(heading.group(1));
            }
        }
        return titles;
    }

    /* Each command line of the scene file, as its words, outside comments and blank lines. */
    static List<List<String>> commandLines(final Path sceneFile) {
        return lines(sceneFile).stream().filter(SceneContent::isCommand).map(line -> words(line.strip())).toList();
    }

    private static boolean isCommand(final String line) {
        return !line.isBlank() && !line.strip().startsWith("#");
    }

    private static void readOutputs(final Path root, final Path sceneFile, final List<SceneContent> scenes) {
        final String name = String.valueOf(sceneFile.getFileName());
        final Media media = new Media(root, name.endsWith(Media.SCENES) ? name.substring(0,
                name.length() - Media.SCENES.length()) : name);
        int index = 0;
        for (final SceneContent scene : scenes) {
            for (final String command : scene.liveCommands) {
                scene.liveOutputs.add(LiveOutputs.read(media, index++, command));
            }
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
                // not a command a slide shows
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
