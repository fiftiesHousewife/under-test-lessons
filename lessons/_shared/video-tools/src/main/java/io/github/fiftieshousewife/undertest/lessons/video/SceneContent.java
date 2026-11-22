package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * What one scene of a video script puts on screen, read from the script itself: the code panels it shows (the same
 * lines {@code show} prints), the commands it runs live, and its callouts. Slide decks draw these as text.
 */
final class SceneContent {

    /** A panel of code: its label, and its lines, each with its line number. */
    record Panel(String label, List<String> lines) {

        Panel {
            lines = List.copyOf(lines);
        }
    }

    private static final Pattern TYPED = Pattern.compile("^Type(?:@\\S+)?\\s+([\"`])(.*)\\1\\s*$");
    private static final Pattern WORD = Pattern.compile("\"([^\"]*)\"|(\\S+)");

    private final List<Panel> panels = new ArrayList<>();
    private final List<String> commands = new ArrayList<>();
    private final List<String> callouts = new ArrayList<>();

    List<Panel> panels() {
        return List.copyOf(panels);
    }

    List<String> commands() {
        return List.copyOf(commands);
    }

    List<String> callouts() {
        return List.copyOf(callouts);
    }

    /** Reads every scene of a script, in order; paths in {@code show} lines are relative to the repository root. */
    static List<SceneContent> read(final Path root, final Path tape) {
        final List<SceneContent> scenes = new ArrayList<>();
        SceneContent current = null;
        for (final String line : lines(tape)) {
            if (line.matches("^#\\s*-+\\s*Scene\\s+\\d+:.*")) {
                current = new SceneContent();
                scenes.add(current);
            } else if (current != null) {
                current.take(root, line);
            }
        }
        return scenes;
    }

    private void take(final Path root, final String line) {
        final Matcher typed = TYPED.matcher(line);
        if (!typed.matches()) {
            return;
        }
        final List<String> words = words(typed.group(2));
        switch (words.getFirst()) {
            case "show" -> panels.add(panel(root, words.subList(1, words.size())));
            case "run_live", "run_with_notes" -> commands.add(words.get(1));
            case "callout" -> callouts.add(words.get(1));
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

    /** The lines {@code show <file> <from> <to> [label]} or {@code show <file> /pattern/ [label]} prints. */
    static Panel panel(final Path root, final List<String> args) {
        final Path file = find(root, args.getFirst());
        final List<String> all = lines(file);
        final String name = String.valueOf(file.getFileName());
        if (args.size() > 1 && args.get(1).length() > 1 && args.get(1).startsWith("/") && args.get(1).endsWith("/")) {
            // awk reads the pattern as a string first, so a doubled backslash in a script means a single one
            final String pattern = args.get(1).substring(1, args.get(1).length() - 1).replace("\\\\", "\\");
            final String label = args.size() > 2 ? args.get(2) : name;
            return new Panel(label, name.endsWith(".toml") ? SourceLines.tomlMatches(all, pattern)
                    : SourceLines.blockAround(all, pattern));
        }
        final int from = args.size() > 1 ? Integer.parseInt(args.get(1)) : 1;
        final int to = args.size() > 2 ? Integer.parseInt(args.get(2)) : all.size();
        return new Panel(args.size() > 3 ? args.get(3) : name, SourceLines.range(all, from, to));
    }

    /** The file a {@code show} line names: a path, an exact file name, or part of a name, as the helper finds it. */
    static Path find(final Path root, final String target) {
        if (Files.isRegularFile(root.resolve(target))) {
            return root.resolve(target);
        }
        final List<Path> files = sourceFiles(root);
        return files.stream().filter(f -> named(f, target + ".java") || named(f, target)).findFirst()
                .or(() -> files.stream().filter(f -> String.valueOf(f.getFileName()).contains(target)).findFirst())
                .orElseThrow(() -> new IllegalArgumentException("show: nothing found for " + target));
    }

    private static boolean named(final Path file, final String name) {
        return String.valueOf(file.getFileName()).equals(name);
    }

    private static List<Path> sourceFiles(final Path root) {
        final List<Path> files = new ArrayList<>();
        for (final String folder : List.of("lessons", "build-logic", "gradle")) {
            if (Files.isDirectory(root.resolve(folder))) {
                try (Stream<Path> walk = Files.walk(root.resolve(folder))) {
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
