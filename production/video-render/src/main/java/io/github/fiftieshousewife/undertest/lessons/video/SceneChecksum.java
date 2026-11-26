package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Stream;

/*
 * What a live-run clip was recorded from: a SHA-256 of its scene's lines and every file of the lesson's example (its
 * code and build file, not its build output), kept beside the clip as scene-<n>.sha256. A clip whose checksum still
 * matches shows what the scene would show today, so it isn't recorded again.
 */
final class SceneChecksum {

    private SceneChecksum() {
    }

    static String of(final Media media, final List<String> scene) {
        final MessageDigest digest = sha256();
        digest.update(String.join("\n", scene).getBytes(StandardCharsets.UTF_8));
        final Path example = media.root().resolve("lessons").resolve(media.safeLesson()).resolve("example");
        exampleFiles(example).forEach(file -> {
            digest.update(example.relativize(file).toString().getBytes(StandardCharsets.UTF_8));
            digest.update(bytes(file));
        });
        return HexFormat.of().formatHex(digest.digest());
    }

    static boolean matches(final Path clips, final int scene, final String checksum) {
        final Path kept = file(clips, scene);
        return Files.isRegularFile(clips.resolve(LiveRunClips.name(scene))) && Files.isRegularFile(kept)
                && checksum.equals(text(kept).strip());
    }

    static void write(final Path clips, final int scene, final String checksum) {
        try {
            Files.writeString(file(clips, scene), checksum + "\n");
        } catch (IOException e) {
            throw new UncheckedIOException("could not write the checksum of scene " + (scene + 1), e);
        }
    }

    private static Path file(final Path clips, final int scene) {
        return clips.resolve("scene-" + (scene + 1) + ".sha256");
    }

    /* The example's files in a fixed order, leaving out what a build writes. */
    private static List<Path> exampleFiles(final Path example) {
        if (!Files.isDirectory(example)) {
            return List.of();
        }
        try (Stream<Path> files = Files.walk(example)) {
            return files.filter(Files::isRegularFile)
                    .filter(file -> !example.relativize(file).startsWith("build"))
                    .sorted().toList();
        } catch (IOException e) {
            throw new UncheckedIOException("could not list " + example, e);
        }
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("this JDK has no SHA-256", e);
        }
    }

    private static byte[] bytes(final Path file) {
        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            throw new UncheckedIOException("could not read " + file, e);
        }
    }

    private static String text(final Path file) {
        return new String(bytes(file), StandardCharsets.UTF_8);
    }
}
