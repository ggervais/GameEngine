#!/usr/bin/perl -w

use strict;

my $obj = $ARGV[0];

open(OBJ, $obj) or die("Can't open OBJ file!");

while (my $line = <OBJ>) {
    if ($line =~ /^f ((?:\d\s?)+)$/) {
        my $parts = split(/\s+/, $line);
        my $info = $1;
        chomp($info);
        my @parts = split(/\s+/, $info);
        my $newString = "f ";
        foreach my $part (@parts) {
            my $intPart = int($part);
            my $newPart = $intPart + 1;
            $newString .= $newPart." ";
        }
        chop($newString);
        print "".$newString."\n";
    } else {
        print $line;
    }
}

close(OBJ) or die("Can't close OBJ file!");
