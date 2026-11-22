package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * What one scene of a video script puts on screen, read from the script itself: the code panels it shows (the same
 * lines {@code show} prints), the commands it runs live with the output kept for each ({@link LiveOutputs}), and its
 * callouts. Slide decks draw these as text.
 */
final class SceneContent {

    /** A panel of code: its label, its lines (each with its line number), and the numbers of the lines marked. */
    record Panel(String label, List<String> lines, Set<Integer> marked) {

        Panel {
            lines = List.copyOf(lines);
            marked = Set.copyOf(marked);
        }
    }

    private static final Pattern MARK_PART = Pattern.compile("(\\d+)(?:-(\\d+))?");

    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final Pattern WORD = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private final List<Panel> shownPanels = new ArrayList<>();
    private final List<String> liveCommands = new ArrayList<>();
    private final List<String> calloutTexts = new ArrayList<>();
    private final List<List<String>> liveOutputs = new ArrayList<>();

    List<Panel> panels() {
        return List.copyOf(shownPanels);
    }

    List<String> commands() {
        return List.copyOf(liveCommands);
    }

    /** For each live command, in the same order, the output kept for it (empty if none). */
    List<List<String>> outputs() {
        return List.copyOf(liveOutputs);
    }

    List<String> callouts() {
        return List.copyOf(calloutTexts);
    }

    /** Reads every scene of a script, in order; paths in {@code show} lines are relative to the repository root. */
    static List<SceneContent> read(final Path root, final Path tape) {
        final Path lesson = tape.toAbsolutePath().resolveSibling("..").normalize();
        final List<Path> places = List.of(lesson, root.resolve("lessons/_shared"), root.resolve("build-logic"),
                root.resolve("gradle"));
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
        final String name = String.valueOf(tape.getFileName());
        final Media media = new Media(root, name.endsWith(".tape") ? name.substring(0, name.length() - 5) : name);
        int index = 0;
        for (final SceneContent scene : scenes) {
            for (final String command : scene.liveCommands) {
                scene.liveOutputs.add(LiveOutputs.read(media, index++, command));
            }
        }
        return scenes;
    }

    private void take(final Path root, final List<Path> places, final String line) {
        final Matcher typed = TYPED.matcher(line);
        if (!typed.matches()) {
            return;
        }
        final List<String> words = words(typed.group(2));
        switch (words.getFirst()) {
            case "show" -> shownPanels.add(panel(root, places, words.subList(1, words.size())));
            case "run_live", "run_with_notes" -> liveCommands.add(words.get(1));
            case "callout" -> calloutTexts.add(words.get(1));
            default -> {
                // notes become the slide's narration; other commands are set-up
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

    /**
     * The lines {@code show <file> <from> <to> [label]} or {@code show <file> /pattern/ [label]} prints, with any
     * {@code --mark 22,24} (or {@code 22-24}) lines marked; {@code --zoom 22-24} marks its lines too.
     */
    static Panel panel(final Path root, final List<Path> places, final List<String> words) {
        final Set<Integer> marked = new TreeSet<>();
        final List<String> args = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (("--mark".equals(words.get(i)) || "--zoom".equals(words.get(i))) && i + 1 < words.size()) {
                marked.addAll(marks(words.get(++i)));
            } else {
                args.add(words.get(i));
            }
        }
        final Panel plain = unmarked(root, places, args);
        return new Panel(plain.label(), plain.lines(), marked);
    }

    /** The lines a {@code show} line zooms in on ({@code --zoom 22-24}), or none. */
    static Set<Integer> zoomed(final List<String> words) {
        final int zoom = words.indexOf("--zoom");
        return zoom >= 0 && zoom + 1 < words.size() ? marks(words.get(zoom + 1)) : Set.of();
    }

    /** The line numbers in a list such as {@code 18,22-24}. */
    static Set<Integer> marks(final String list) {
        final Set<Integer> numbers = new TreeSet<>();
        for (final String part : list.split(",")) {
            final Matcher range = MARK_PART.matcher(part.strip());
            if (!range.matches()) {
                throw new IllegalArgumentException("show --mark: not a line number or range: " + part);
            }
            final int from = Integer.parseInt(range.group(1));
            final int to = range.group(2) == null ? from : Integer.parseInt(range.group(2));
            for (int n = from; n <= to; n++) {
                numbers.add(n);
            }
        }
        return numbers;
    }

    private static Panel unmarked(final Path root, final List<Path> places, final List<String> args) {
        final Path file = find(root, places, args.getFirst());
        final List<String> all = lines(file);
        final String name = String.valueOf(file.getFileName());
        if (args.size() > 1 && isPattern(args.get(1))) {
            // awk reads the pattern as a string first, so a doubled backslash in a script means a single one
            final String pattern = args.get(1).substring(1, args.get(1).length() - 1).replace("\\\\", "\\");
            final String label = args.size() > 2 ? args.get(2) : name;
            return new Panel(label, name.endsWith(".toml") ? SourceLines.tomlMatches(all, pattern)
                    : SourceLines.blockAround(all, pattern), Set.of());
        }
        final int from = args.size() > 1 ? Integer.parseInt(args.get(1)) : 1;
        final int to = args.size() > 2 ? Integer.parseInt(args.get(2)) : all.size();
        return new Panel(args.size() > 3 ? args.get(3) : name, SourceLines.range(all, from, to), Set.of());
    }

    private static boolean isPattern(final String arg) {
        return arg.length() > 1 && arg.startsWith("/") && arg.endsWith("/");
    }

    /**
     * The file a {@code show} line names: a path, an exact file name, or part of a name, as the helper finds it,
     * searching the lesson's own folder before the shared ones, so a name used in two lessons finds this lesson's file.
     */
    static Path find(final Path root, final List<Path> places, final String target) {
        if (Files.isRegularFile(root.resolve(target))) {
            return root.resolve(target);
        }
        final List<Path> files = sourceFiles(root, places);
        return files.stream().filter(f -> named(f, target + ".java") || named(f, target)).findFirst()
                .or(() -> files.stream().filter(f -> String.valueOf(f.getFileName()).contains(target)).findFirst())
                .orElseThrow(() -> new IllegalArgumentException("show: nothing found for " + target));
    }

    private static boolean named(final Path file, final String name) {
        return String.valueOf(file.getFileName()).equals(name);
    }

    private static List<Path> sourceFiles(final Path root, final List<Path> places) {
        final List<Path> files = new ArrayList<>();
        for (final Path folder : places) {
            if (Files.isDirectory(folder)) {
                try (Stream<Path> walk = Files.walk(folder)) {
                    walk.filter(Files::isRegularFile).filter(f -> !insideBuildFolder(root.relativize(f))).sorted()
                            .forEach(files::add);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return files;
    }

    /** True for generated files: anything under a folder named build, inside the repository. */
    private static boolean insideBuildFolder(final Path relative) {
        for (final Path part : relative) {
            if ("build".equals(part.toString())) {
                return true;
            }
        }
        return false;
    }

    private static List<String> lines(final Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
