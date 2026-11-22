# Narration: capturing what an old service receives and sends

One item per scene of [capturing-with-logbook.tape](capturing-with-logbook.tape). The intro and closing slides have
their own narration, in [capturing-with-logbook.slides.md](capturing-with-logbook.slides.md). Written to be spoken.

1. Step one. Add Logbook to the version catalogue, with one version for its 4 modules. The old service runs on Jetty
   12, and calls the loyalty provider with Apache's HTTP client, so those are added too.
2. Step two. Add the modules the lesson needs: Logbook's core, its JSON filters, its servlet filter, and its hook for
   Apache's HTTP client 5.
3. Here is where Logbook hooks into the old service. On the server, its servlet filter sits in front of every request
   the service receives. In the loyalty client, its handler wraps every call the service sends. Both are given the
   same Logbook.
4. Here is the code under test, today's capture set-up. It builds a Logbook that writes each message as plain HTTP.
   Someone added one header filter, to remove the headers that change on every run, such as the date. That one filter
   replaces Logbook's default, the one that hides the Authorization header. And nothing touches the email address in
   the bodies.
5. This is [pause 300ms] the weak test. It asks the old service for a customer, then checks that the capture recorded
   what the service received, what it sent, and both answers. It never reads what was written. So a token, or an
   email address, in the capture passes.
6. Now, [pause 300ms] the strong tests. The first lists every secret the capture holds: the caller's token, the
   loyalty provider's key, and the customer's email. The list must be empty. The second compares the whole capture
   with its approved file, line for line.
7. We run the tests against today's capture. The weak test passes. Both strong tests fail. The capture holds all 3
   secrets, and the approval summary shows where. Both Authorization headers carry the real tokens, and both bodies
   carry the customer's real email address.
8. Now the filtered capture. It keeps Logbook's Authorization filter beside its own, and a JSON body filter replaces
   every email field with an address that can't be anyone's. We run the same tests. [pause 1s] Every test passes.
9. Here is the approved capture, the file a person checked, and the tests compare against. The service received the
   request with its token replaced by 3 X's. It sent the loyalty provider a request with the key hidden too, and a
   stand-in email address. Both answers follow. This file is safe to keep beside the tests.
