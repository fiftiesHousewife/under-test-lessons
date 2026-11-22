#!/usr/bin/env perl
# What a live run in a lesson video shows: a command's output with Gradle's padding taken out, so one run fits one
# screen. Used by run_live (callout.sh) as the video records, and by tools/capture-lesson-outputs.py, so the outputs
# kept for the checks and the decks are exactly what the video shows.
#
#   some-command 2>&1 | perl lessons/_shared/screen-output.pl
#
# It takes out only padding, never a result:
#   - blank lines, which Gradle prints after every test's result line;
#   - Gradle's general advice after a failure ("* Try:" and its "> Run with --stacktrace" lines), and its
#     "FAILURE: Build failed with an exception." heading; "* What went wrong:" and its reason stay.
use strict;
use warnings;

$| = 1;    # pass each line on at once, so the video shows the run as it happens
my $in_advice = 0;
while (my $line = <STDIN>) {
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
    print $line;
}
