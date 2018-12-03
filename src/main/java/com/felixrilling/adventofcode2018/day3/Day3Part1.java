package com.felixrilling.adventofcode2018.day3;

import com.felixrilling.adventofcode2018.AdventOfCodeRuntimeException;
import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class Day3Part1 {
    public static void main(String[] args) {
        System.out.println(new FabricOverlapChecker(AdventOfCodeUtils.getInput("day3/input.txt")).calculate());
    }

    private static class FabricOverlapChecker {
        private static final int FIELD_LENGTH_X = 1000;
        private static final int FIELD_LENGTH_Y = 1000;
        private final String input;

        FabricOverlapChecker(String input) {
            this.input = input;
        }

        int calculate() {
            List<Claim> claims = input
                .lines()
                .map(Claim::ofString)
                .collect(Collectors.toList());

            return calculateOverlap(
                claims
            );
        }

        private int calculateOverlap(List<Claim> claims) {
            int overlaps = 0;

            for (int x = 0; x < FabricOverlapChecker.FIELD_LENGTH_X; x++) {
                for (int y = 0; y < FabricOverlapChecker.FIELD_LENGTH_Y; y++) {
                    int claimed = 0;
                    for (Claim claim : claims) {
                        // n^3 is still better than x^n right? :)))
                        if (claim.claimsPosition(x, y)) {
                            if (claimed > 0) {
                                // NeStInG sHoUlD nOt Be MoRe tHaN 3 lAyErS.
                                overlaps++;
                                break;
                            }

                            claimed++;
                        }
                    }
                }
            }

            return overlaps;
        }

        private static class Claim {

            private static final String PATTERN_START_X = "startX";
            private static final String PATTERN_START_Y = "startY";
            private static final String PATTERN_LENGTH_X = "lengthX";
            private static final String PATTERN_LENGTH_Y = "lengthY";
            private static final Pattern PATTERN = Pattern.compile(
                ".+@ (?<" + PATTERN_START_X + ">\\d+),(?<" + PATTERN_START_Y + ">\\d+):" +
                    " (?<" + PATTERN_LENGTH_X + ">\\d+)x(?<" + PATTERN_LENGTH_Y + ">\\d+)");

            private final int startX;
            private final int startY;
            private final int endX;
            private final int endY;

            Claim(int startX, int startY, int lengthX, int lengthY) {
                this.startX = startX;
                this.startY = startY;
                this.endX = startX + lengthX - 1;
                this.endY = startY + lengthY - 1;
            }

            static Claim ofString(String string) {
                Matcher matcher = PATTERN.matcher(string);
                if (!matcher.find())
                    throw new AdventOfCodeRuntimeException("Regex did not find any match!");
                String startX = matcher.group(PATTERN_START_X);
                String startY = matcher.group(PATTERN_START_Y);
                String lengthX = matcher.group(PATTERN_LENGTH_X);
                String lengthY = matcher.group(PATTERN_LENGTH_Y);

                if (startX == null || startY == null || lengthX == null || lengthY == null)
                    throw new AdventOfCodeRuntimeException("Regex did not capture all values!");

                return new Claim(Integer.valueOf(startX), Integer.valueOf(startY), Integer.valueOf(lengthX), Integer.valueOf(lengthY));
            }

            boolean claimsPosition(int x, int y) {
                boolean xMatches = x >= startX && x <= endX;
                boolean yMatches = y >= startY && y <= endY;
                return xMatches && yMatches;
            }
        }
    }
}
