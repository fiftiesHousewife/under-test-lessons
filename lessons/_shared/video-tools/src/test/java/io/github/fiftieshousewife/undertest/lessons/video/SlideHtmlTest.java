package io.github.fiftieshousewife.undertest.lessons.video;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SlideHtmlTest {

    @TempDir
    Path root;

    @AfterEach
    void restore() {
        System.clearProperty("video.browser");
        System.clearProperty("video.slideCss");
    }

    @Test
    void buildsAPageStyledByEveryStylesheetInOrderWithThePictureInline() throws IOException {
        final Path base = Files.writeString(root.resolve("base.css"), "h1 { color: teal; }");
        final Path user = Files.writeString(root.resolve("user.css"), "h1 { color: navy; }");
        Files.writeString(root.resolve("bug.svg"), "<svg id=\"bug\"></svg>");
        final String page = SlideHtml.page(new Slide(new Slide.Heading("Tests & <bugs>", "bug"),
                List.of("A point", "https://pitest.org"), "Say it."), List.of(base, user), List.of(root));

        assertAll(
                () -> assertThat(page.indexOf("teal")).isLessThan(page.indexOf("navy")),
                () -> assertThat(page).contains("<h1>Tests &amp; &lt;bugs&gt;</h1>"),
                () -> assertThat(page).contains("<li class=\"point\">A point</li>"),
                () -> assertThat(page).contains("<li class=\"link\">https://pitest.org</li>"),
                () -> assertThat(page).contains("<figure class=\"picture\"><svg id=\"bug\"></svg></figure>"),
                () -> assertThat(SlideHtml.page(new Slide(new Slide.Heading("No picture", ""), List.of(), "x"),
                        List.of(base), List.of(root))).doesNotContain("figure"),
                () -> assertThatIllegalArgumentException().isThrownBy(() -> SlideHtml.page(new Slide(
                        new Slide.Heading("x", "missing"), List.of(), "x"), List.of(base), List.of(root))));
    }

    @Test
    void linksALessonPointToThatLessonsDeckWithoutAnyAddress() {
        final Slide slide = new Slide(new Slide.Heading("Can we trust the tests?", ""),
                List.of("lesson: Mutation testing (mutation-testing)", "Black-box tests (coming)"), "Say.");

        final String body = SlideHtml.body(slide, List.of(), "");

        assertAll(
                () -> assertThat(body).contains("<li class=\"link lesson\"><a href=\"../../mutation-testing/media/"
                        + "mutation-testing.deck.html#1\">Mutation testing</a></li>"),
                () -> assertThat(body).contains("<li class=\"point\">Black-box tests (coming)</li>"),
                () -> assertThat(SlideHtml.lessonLinks(slide)).containsExactly("mutation-testing"));
    }

    @Test
    void drawsSlidesWithTheBrowserAndAddsTheUserStylesheet() throws IOException {
        final Path shared = Files.createDirectories(root.resolve("lessons/_shared/slides/pictures"));
        Files.writeString(shared.resolveSibling("slides.css"), "body { }");
        Files.writeString(shared.resolve("globe.svg"), "<svg></svg>");
        final Path media = Files.createDirectories(root.resolve("lessons/demo/media"));
        Files.writeString(media.resolve("demo.slides.md"), "## intro: Hello\n- A point\nPicture: globe\nSay: Hi.\n"
                + "## closing: Bye\nSay: Bye.\n");
        final Path browser = root.resolve("browser");
        Files.writeString(browser, "#!/bin/sh\nfor a; do case \"$a\" in --screenshot=*) touch \"${a#--screenshot=}\";;"
                + " esac; done\n");
        assertThat(browser.toFile().setExecutable(true)).isTrue();
        System.setProperty("video.browser", browser.toString());
        System.setProperty("video.slideCss", Files.writeString(root.resolve("brand.css"), "h1 { }").toString());
        final Media demo = new Media(root, "demo");

        assertAll(
                () -> assertThat(SlideDrawing.drawAll(demo)).hasSize(2).allSatisfy(png -> assertThat(png).exists()),
                () -> assertThat(SlideDrawing.stylesheets(demo)).last().isEqualTo(root.resolve("brand.css")),
                () -> assertThat(Browser.program()).isEqualTo(browser.toString()),
                () -> assertThat(withoutBrowserSetting()).isEqualTo("google-chrome"));
    }

    private static String withoutBrowserSetting() {
        System.clearProperty("video.browser");
        return Browser.program();
    }
}
