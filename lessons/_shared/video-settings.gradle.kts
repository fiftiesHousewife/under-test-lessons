// Settings for slides, decks and videos, shared by the inside video tools (lessons/_shared/video-tools) and the
// production ones (production/video-render), so a slide is drawn the same way wherever it is drawn.
// Slides are styled by lessons/_shared/slides/slides.css, then any user stylesheet: -PlessonSlideCss=<path>.
// A relative path is taken from the repository root, where the commands are run.
val repositoryDir = rootDir
val slideCss = providers.gradleProperty("lessonSlideCss").filter { it.isNotBlank() }
    .map { File(it).let { css -> if (css.isAbsolute) css else repositoryDir.resolve(it) }.absolutePath }.orElse("")
// Slides are screenshotted by a headless browser: -PlessonBrowser=<program>, or Google Chrome where it usually is.
val macChrome = "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
val slideBrowser = providers.gradleProperty("lessonBrowser")
    .orElse(if (File(macChrome).canExecute()) macChrome else "google-chrome")
// Your own pictures, by name, used before the shared ones: -PlessonPictures=<folder> (for example the ink set,
// lessons/_shared/slides/pictures/ink). Set it, and lessonSlideCss, in gradle.properties to use them on every run.
val pictureFolder = providers.gradleProperty("lessonPictures").filter { it.isNotBlank() }
    .map { File(it).let { dir -> if (dir.isAbsolute) dir else repositoryDir.resolve(it) }.absolutePath }.orElse("")
// Where the lessons and the toolkit live, for the links on slides: lessonsRepoUrl and toolkitRepoUrl in
// gradle.properties. Unset, slides describe the repositories without an address.
val lessonsRepoUrl = providers.gradleProperty("lessonsRepoUrl").orElse("")
val toolkitRepoUrl = providers.gradleProperty("toolkitRepoUrl").orElse("")
// How pictures are framed on slides: soft (a blurred round edge), none, or circle. lessonPictureFrame in gradle.properties.
val pictureFrame = providers.gradleProperty("lessonPictureFrame").orElse("soft")
// Burned-in captions are drawn with Java's 2D graphics, which needs no screen: run headless everywhere.
val burnCaptions = providers.gradleProperty("lessonBurnCaptions").orElse("true")
tasks.withType<Test>().configureEach {
    systemProperty("java.awt.headless", "true")
}
tasks.withType<JavaExec>().configureEach {
    systemProperty("java.awt.headless", "true")
    systemProperty("video.burnCaptions", burnCaptions.get())
    systemProperty("video.pictureFrame", pictureFrame.get())
    systemProperty("video.lessonsRepo", lessonsRepoUrl.get())
    systemProperty("video.toolkitRepo", toolkitRepoUrl.get())
    systemProperty("video.pictures", pictureFolder.get())
    systemProperty("video.slideCss", slideCss.get())
    systemProperty("video.browser", slideBrowser.get())
}

