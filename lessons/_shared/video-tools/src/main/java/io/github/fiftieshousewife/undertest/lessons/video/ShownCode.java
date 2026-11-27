package io.github.fiftieshousewife.undertest.lessons.video;

import io.github.fiftieshousewife.undertest.lessons.video.SceneContent.Panel;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/* The code panel a scene file's {@code show} line puts on its slide. */
final class ShownCode {

    private ShownCode() {
    }

    /*
     * The lines {@code show <file> <from> <to> [label]} or {@code show <file> /pattern/ [label]} prints, with any
     * {@code --mark 22,24} (or {@code 22-24}) lines marked; {@code --zoom 22-24} marks its lines too.
     */
    static Panel panel(final Path root, final List<Path> places, final List<String> words) {
        final Set<Integer> marked = new TreeSet<>();
        final List<String> args = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            if (("--mark".equals(words.get(i)) || "--zoom".equals(words.get(i))) && i + 1 < words.size()) {
                marked.addAll(MarkedLines.marks(words.get(++i)));
            } else {
                args.add(words.get(i));
            }
        }
        final Panel plain = unmarked(root, places, args);
        return new Panel(plain.label(), plain.lines(), marked);
    }

    /* Where a scene file's {@code show} lines look for files: its lesson's folder first, then the shared ones. */
    static List<Path> places(final Path root, final Path sceneFile) {
        final Path lesson = sceneFile.toAbsolutePath().resolveSibling("..").normalize();
        return List.of(lesson, root.resolve("lessons/_shared"), root.resolve("build-logic"), root.resolve("gradle"));
    }

    private static Panel unmarked(final Path root, final List<Path> places, final List<String> args) {
        final Path file = find(root, places, args.getFirst());
        if (args.size() > 1 && isPattern(args.get(1))) {
            return patternPanel(file, args);
        }
        return rangePanel(file, args);
    }

    /* The lines {@code show <file> [<from> [<to> [label]]]} prints. */
    private static Panel rangePanel(final Path file, final List<String> args) {
        final List<String> all = lines(file);
        final int from = args.size() > 1 ? Integer.parseInt(args.get(1)) : 1;
        final int to = args.size() > 2 ? Integer.parseInt(args.get(2)) : all.size();
        return new Panel(args.size() > 3 ? args.get(3) : name(file), SourceLines.range(all, from, to), Set.of());
    }

    /* The lines {@code show <file> /pattern/ [label]} prints. */
    private static Panel patternPanel(final Path file, final List<String> args) {
        final List<String> all = lines(file);
        final String name = name(file);
        // a doubled backslash in a scene file's pattern means a single one, as it did for the old shell helper's awk
        final String pattern = args.get(1).substring(1, args.get(1).length() - 1).replace("\\\\", "\\");
        final String label = args.size() > 2 ? args.get(2) : name;
        return new Panel(label, name.endsWith(".toml") ? SourceLines.tomlMatches(all, pattern)
                : SourceLines.blockAround(all, pattern), Set.of());
    }

    private static String name(final Path file) {
        return String.valueOf(file.getFileName());
    }

    private static boolean isPattern(final String arg) {
        return arg.length() > 1 && arg.startsWith("/") && arg.endsWith("/");
    }

    /*
     * The file a {@code show} line names: a path, an exact file name, or part of a name,
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

    /* True for generated files: anything under a folder named build, inside the repository. */
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
