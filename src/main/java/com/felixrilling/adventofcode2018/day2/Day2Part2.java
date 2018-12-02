package com.felixrilling.adventofcode2018.day2;

import com.felixrilling.adventofcode2018.AdventOfCodeRuntimeException;
import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.util.Set;
import java.util.stream.Collectors;

public class Day2Part2 {
    public static void main(String[] args) {
        System.out.println(new BoxHashCalculator(AdventOfCodeUtils.getInput("day2/input.txt")).calculate());
    }

    private static class BoxHashCalculator {
        private final String input;

        BoxHashCalculator(String input) {
            this.input = input;
        }

        String calculate() {
            Set<String> lines = input.lines().collect(Collectors.toSet());

            String wordA = null;
            String wordB = null;

            for (String primary : lines) {
                for (String secondary : lines) {
                    if (hasDifferenceOfSize(primary, secondary, 1)) {
                        wordA = primary;
                        wordB = secondary;
                    }
                }
            }

            if (wordA == null || wordB == null)
                throw new AdventOfCodeRuntimeException("No difference of 1 found.");

            return findCommonLetters(wordA, wordB);
        }


        private boolean hasDifferenceOfSize(String primary, String secondary, int size) {
            int differences = 0;
            for (int i = 0; i < primary.length(); i++) {
                if (differences > size)
                    return false;

                if (primary.charAt(i) != secondary.charAt(i))
                    differences++;
            }

            return differences == size;
        }


        private String findCommonLetters(String primary, String secondary) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < primary.length(); i++) {
                if (primary.charAt(i) == secondary.charAt(i))
                    sb.append(primary.charAt(i));
            }

            return sb.toString();
        }
    }
}
