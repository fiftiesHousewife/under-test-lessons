#!/usr/bin/env perl
# What a live run in a lesson video shows: a command's output with Gradle's padding taken out, so one run fits one
# screen. Used by run_live (callout.sh) as the video records, and by tools/capture-lesson-outputs.py, so the outputs
# kept for the checks and the decks are exactly what the video shows.
#
#   some-command 2>&1 | perl lessons/_shared/screen-output.pl
#
# It takes out only padding, never a result:
#   - blank lines, which Gradle prints after every test's result line;
#   - the JVM's warning that class sharing is off when an agent extends the boot class path,
#   - the JDK's warnings about Lombok's use of sun.misc.Unsafe, and about Gradle's native library, which a newer JDK
#     prints before a build starts;
#   - Gradle's general advice after a failure ("* Try:" and its "> Run with --stacktrace" lines), and its
#     "FAILURE: Build failed with an exception." heading;
#   - the "* What went wrong:" block of the build's own test summary task (testSummary), which only repeats the line
#     "N tests completed, M failed" printed just above it. Any other "* What went wrong:" and its reason stay.
use strict;
use warnings;

$| = 1;    # pass each line on at once, so the video shows the run as it happens
my $in_advice = 0;
my $held = undef;    # a "* What went wrong:" line, held until the next line says what failed
while (my $line = <STDIN>) {
    if (defined $held) {
        my $summary = $line =~ /^Execution failed for task '[^']*:\w*([Ff]ailures|[Ss]ummary)'/;
        print $held unless $summary;
        $held = undef;
        if ($summary) {
            $in_advice = 1;    # its "> N of M tests failed" line goes with it
            next;
        }
    }
    if ($line =~ /^\* What went wrong:/) {
        $held = $line;
        next;
    }
    if ($line =~ /^\* Try:/) {
        $in_advice = 1;
        next;
    }
    if ($in_advice) {
        next if $line =~ /^> / || $line =~ /^\s*$/;
        $in_advice = 0;
    }
    next if $line =~ /^\s*$/;
    next if $line =~ /^FAILURE: Build failed with an exception\.$/;
    next if $line =~ /^OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes/;
    next if $line =~ /^WARNING: .*(sun\.misc\.Unsafe|lombok\.permit|terminally deprecated method|restricted method|System::load|--enable-native-access|Restricted methods will be blocked)/;
    print $line;
}
