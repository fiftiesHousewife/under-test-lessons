package io.github.fiftieshousewife.undertest.lessons.video;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/* Where a lesson's deck and the files it is built from live: lessons/<id>/media/<id>.*. */
record Media(Path root, String lesson) {

    static final String SCENES = ".scenes";

    /* The lesson named, or every lesson with a slides file when the name is "all". */
    static List<Media> lessons(final Path root, final String lesson) {
        if (!"all".equals(lesson)) {
            return List.of(new Media(root, lesson));
        }
        try (Stream<Path> folders = Files.list(root.resolve("lessons"))) {
            return folders.map(folder -> new Media(root, String.valueOf(folder.getFileName())))
                    .filter(media -> Files.isRegularFile(media.file(".slides.md")))
                    .sorted(Comparator.comparing(Media::safeLesson)).toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Path folder() {
        return root.resolve("lessons").resolve(lesson).resolve("media");
    }

    Path file(final String suffix) {
        return folder().resolve(lesson + suffix);
    }

    String name(final String suffix) {
        return lesson + suffix;
    }

    /* The lesson's scene file: what each scene's slide shows. */
    Path scenes() {
        return file(SCENES);
    }

    /* The lesson's slides, with shared slides from lessons/_shared/slides. */
    Slides slides() {
        return Slides.read(file(".slides.md"), sharedSlides());
    }

    /* Where pictures come from: the folder in {@code video.pictures} (-PlessonPictures), then the shared ones. */
    List<Path> pictureFolders() {
        final Path shared = sharedSlides().resolve("pictures");
        final String chosen = System.getProperty("video.pictures", "");
        return chosen.isEmpty() ? List.of(shared) : List.of(Path.of(chosen), shared);
    }

    Path sharedSlides() {
        return root.resolve("lessons/_shared/slides");
    }

    String safeLesson() {
        return lesson.replace('\r', ' ').replace('\n', ' ');
    }
}
