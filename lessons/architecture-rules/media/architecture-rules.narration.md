# Narration: architecture rules

One item per scene of [architecture-rules.scenes](architecture-rules.scenes). The intro and closing slides have their
own narration, in [architecture-rules.slides.md](architecture-rules.slides.md). Written to be spoken.

1. Step one. Add ArchUnit to the version catalogue.
2. Step two. Add it as a test dependency of the lesson. There is nothing to configure: ArchUnit is an ordinary library,
   used from ordinary tests.
3. Here is the code under test, the shop's admin page as it is today. It shows how many orders there are. To get the
   number, it goes straight to the storage layer, skipping the service layer and the shop's rules. A review can easily
   miss it. But every shortcut like this makes the next change harder, and in old code, shortcuts pile up quietly.
4. These are ordinary unit tests. They check that the page gives the right answer, both today and after the fix. It
   does, so the tests pass, whichever way the page gets its number.
5. Next, the architecture rule: a test of how the code is put together, rather than what it does. First, ArchUnit
   imports the compiled classes of each layer's package. Reading compiled code, it checks every call, field and
   parameter. The rule names three layers, web, service and storage, each by its package. The web layer may use the
   service. The service may use storage. And nothing else may reach storage. The last line hands the result to a small
   helper of the lesson's own, which only shortens the package names in ArchUnit's report.
6. We run the tests against the page as it is today. The unit tests pass. The rule fails, and names every place the web
   layer reaches into storage: the constructor, the field, and the method call.
7. Now the page that asks the service layer instead. We run the same tests. [pause 1s] Every rule holds.
