package com.felixrilling.adventofcode2018.day2;

import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.util.HashMap;
import java.util.stream.Collectors;

public class Day2Part1 {
    public static void main(String[] args) {
        System.out.println(new BoxHashCalculator(AdventOfCodeUtils.getInput("day2/input.txt")).calculate());
    }

    private static class BoxHashCalculator {
        private final String input;

        BoxHashCalculator(String input) {
            this.input = input;
        }

        int calculate() {
            int countPairs = 0;
            int countTriplets = 0;

            for (String line : input.lines().collect(Collectors.toList())) {
                if (containsSameOfSize(line, 2))
                    countPairs++;
                if (containsSameOfSize(line, 3))
                    countTriplets++;
            }

            return countPairs * countTriplets;
        }


        private boolean containsSameOfSize(String line, int size) {
            return countLetters(line).entrySet().stream().anyMatch(entry -> entry.getValue() == size);
        }

        private HashMap<Character, Integer> countLetters(String val) {
            HashMap<Character, Integer> result = new HashMap<>(val.length());

            for (int i = 0; i < val.length(); i++) {
                Character letter = val.charAt(i);

                result.put(letter, result.containsKey(letter) ? result.get(letter) + 1 : 1);
            }

            return result;
        }
    }
}
