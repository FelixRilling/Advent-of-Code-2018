package com.felixrilling.adventofcode2018.day1;

import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Day1Part2 {

    public static void main(String[] args) {
        System.out.println(new FrequencyCalculator(AdventOfCodeUtils.getInput("day1/input.txt")).calculate());
    }

    private static class FrequencyCalculator {

        private final String input;

        FrequencyCalculator(String input) {
            this.input = input;
        }

        String calculate() {
            int val = 0;
            Collection<Integer> valuesReached = new HashSet<>();
            valuesReached.add(val);

            while (true) {
                for (String line : input.lines().collect(Collectors.toList())) {
                    val += resolveAdjustment(line);

                    if (valuesReached.contains(val))
                        return Integer.toString(val);

                    valuesReached.add(val);
                }
            }
        }

        private int resolveAdjustment(String line) {
            int val = Integer.parseInt(line.substring(1));

            return line.startsWith("-") ? -val : val;
        }
    }
}
