# Narration: capturing what an old service receives and sends

One item per scene of [capturing-with-logbook.tape](capturing-with-logbook.tape). The intro and closing slides have
their own narration, in [capturing-with-logbook.slides.md](capturing-with-logbook.slides.md). Written to be spoken.

1. Step one. Add Logbook to the version catalogue, with one version for its 4 modules. The old service runs on Jetty
   12, and calls the loyalty provider with Apache's HTTP client, so we add those too. ApprovalTests, which keeps the
   approved file, is already there.
2. Step two. Add the modules the lesson needs. [pause 400ms] Logbook's core, [pause 300ms] its JSON filters,
   [pause 300ms] its servlet filter, [pause 300ms] and its hook for Apache's HTTP client 5. [pause 400ms] Last, the
   tests get ApprovalTests.
3. Here is where Logbook hooks into the old service, to record from inside it. On the server, Logbook's servlet
   filter, a hook in the web server, sits in front of every request the service receives. In the loyalty client, its
   handler wraps every call the service sends. We give both the same Logbook.
4. Here is the code under test, today's capture set-up. It builds a Logbook that writes each message as plain HTTP.
   It has one header filter of its own, to remove the headers that change on every run, such as the date. That filter
   replaces Logbook's default, the one that hides the Authorization header. And nothing touches the email address in
   the bodies.
5. This is the weak test. It asks the old service for a customer, then checks that the recording has an entry for
   what the service received, what it sent, and both answers. It never checks what those entries say. So a token, or
   an email address, in the recording doesn't fail it.
6. Now, the strong tests. The first lists every secret the recording holds: the caller's token, the loyalty provider's
   key, and the customer's email. The list must be empty. The second hands the whole recording to ApprovalTests, which
   compares it with its approved file, line for line. When they differ, ApprovalTests writes the new recording beside
   it, as a received file. A person checks that file once, and approves it by renaming it as the approved file.
7. We run the tests against today's capture. The weak test passes. Both strong tests fail. The recording holds all 3
   secrets, and the approval summary shows where. Both Authorization headers carry the real tokens, and both bodies
   carry the customer's real email address. Once a secret is in a log or a file, it is hard to take back.
8. Next, the filtered capture. It puts Logbook's Authorization filter back, beside its own header filter. That filter
   writes XXX in place of each token. And a JSON body filter replaces every email field with a made-up address that no
   real person uses. We run the same tests. [pause 1s] Every test passes.
9. Last, the approved capture: the file a person checked, which the tests compare against. The service received the
   request with its token replaced by XXX. It sent the loyalty provider a request with the key hidden too, and a
   stand-in email address. Both answers follow. This file is safe to keep beside the tests.
