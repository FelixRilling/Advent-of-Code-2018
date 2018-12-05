package com.felixrilling.adventofcode2018.day5;

import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

@SuppressWarnings("Duplicates")
public class Day5Part1 {
    public static void main(String[] args) {
        System.out.println(new PolymerReactor(AdventOfCodeUtils.getInput("day5/input.txt")).calculate());
    }

    private static class PolymerReactor {
        private final String input;

        PolymerReactor(String input) {
            this.input = input.replace("\n", "");
        }

        int calculate() {
            int lastLength = Integer.MAX_VALUE;
            String current = input;

            while (current.length() < lastLength) {
                lastLength = current.length();
                current = stripReacting(current);
            }

            return current.length();
        }

        private String stripReacting(String current) {
            StringBuilder builder = new StringBuilder();

            int i;
            for (i = 0; i < current.length(); i++) {
                if (i == current.length() - 1 || !areReactive(current.charAt(i), current.charAt(i + 1)))
                    builder.append(current.charAt(i));
                else
                    i++;
            }

            return builder.toString();
        }

        private boolean areReactive(char letterCurrent, char letterNext) {
            return String.valueOf(letterCurrent).equalsIgnoreCase(String.valueOf(letterNext)) && letterCurrent != letterNext;
        }
    }
}
