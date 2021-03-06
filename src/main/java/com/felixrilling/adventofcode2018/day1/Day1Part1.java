package com.felixrilling.adventofcode2018.day1;

import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

public class Day1Part1 {

    public static void main(String[] args) {
        System.out.println(new FrequencyCalculator(AdventOfCodeUtils.getInput("day1/input.txt")).calculate());
    }

    private static class FrequencyCalculator {

        private final String input;

        FrequencyCalculator(String input) {
            this.input = input;
        }

        String calculate() {
            int val = input.lines().mapToInt(this::resolveAdjustment).sum();

            return Integer.toString(val);
        }

        private int resolveAdjustment(String line) {
            int val = Integer.parseInt(line.substring(1));

            return line.startsWith("-") ? -val : val;
        }
    }
}
