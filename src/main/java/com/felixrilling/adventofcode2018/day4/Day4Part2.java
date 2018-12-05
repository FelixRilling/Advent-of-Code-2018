package com.felixrilling.adventofcode2018.day4;

import com.felixrilling.adventofcode2018.AdventOfCodeRuntimeException;
import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// FIXME Incorrect
@SuppressWarnings("Duplicates")
public class Day4Part2 {
    public static void main(String[] args) {
        System.out.println(new GuardSleepAnalyzer(AdventOfCodeUtils.getInput("day4/input.txt")).calculate());
    }

    private static class GuardSleepAnalyzer {
        private final String input;

        GuardSleepAnalyzer(String input) {
            this.input = input;
        }

        int calculate() {
            final List<GuardLogEntry> guardLogEntries = input
                .lines()
                .map(GuardLogEntry::ofLine)
                .sorted(Comparator.comparing(GuardLogEntry::getTimestamp))
                .collect(Collectors.toList());
            List<GuardShift> guardShifts = GuardShift.ofLogEntries(guardLogEntries);

            int bestGuardId = 0;
            int bestMinute = -1;
            Map<Integer, Map<Integer, Integer>> guardSleepMap = createGuardSleepMap(guardShifts);
            for (Map.Entry<Integer, Map<Integer, Integer>> guard : guardSleepMap.entrySet()) {
                Optional<Map.Entry<Integer, Integer>> bestEntryOfGuard = guard.getValue().entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue));
                if (bestEntryOfGuard.isPresent() && bestEntryOfGuard.get().getValue() > bestMinute) {
                    bestGuardId = guard.getKey();
                    bestMinute = bestEntryOfGuard.get().getKey();
                }
            }

            return bestGuardId * bestMinute;
        }

        private Map<Integer, Map<Integer, Integer>> createGuardSleepMap(List<GuardShift> guardShifts) {
            Map<Integer, Map<Integer, Integer>> guardSleep = new HashMap<>();
            for (GuardShift shift : guardShifts) {
                Map<Integer, Integer> currentSleepMap = guardSleep.containsKey(shift.getGuardId())
                    ? guardSleep.get(shift.getGuardId())
                    : new HashMap<>();
                guardSleep.put(shift.getGuardId(), addSleepValuesOfShift(currentSleepMap, shift));
            }
            return guardSleep;
        }

        private Map<Integer, Integer> addSleepValuesOfShift(Map<Integer, Integer> sleepMap, GuardShift shift) {
            for (GuardNap nap : shift.getNaps()) {
                int napStartMin = Date.from(nap.getNapStart()).getMinutes();
                int napEndMin = Date.from(nap.getNapEnd()).getMinutes();

                for (int minute = 0; minute < 60; minute++) {
                    if (minute >= napStartMin && minute < napEndMin)
                        sleepMap.put(minute, sleepMap.containsKey(minute) ? sleepMap.get(minute) + 1 : 1);
                }
            }
            return sleepMap;
        }


        /**
         * GuardLogEntry
         */
        private static class GuardLogEntry {
            private static final Pattern PATTERN_BEGINS_SHIFT = Pattern.compile("Guard #(.+) begins shift");
            private static final Pattern PATTERN_WAKES_UP = Pattern.compile("wakes up");
            private static final Pattern PATTERN_FALLS_ASLEEP = Pattern.compile("falls asleep");
            private static final Pattern PATTERN_TIMESTAMP = Pattern.compile("\\[(.+)]");
            private static final String FORMAT_TIMESTAMP = "yyyy-MM-dd kk:mm";
            private final Instant timestamp;
            private final Action action;
            private final Integer guardId;
            GuardLogEntry(Instant timestamp, Action action, Integer guardId) {
                this.timestamp = timestamp;
                this.action = action;
                this.guardId = guardId;
            }

            static GuardLogEntry ofLine(String line) {
                Integer guardId = null;
                Action action;

                final Matcher beginsShiftMatcher = PATTERN_BEGINS_SHIFT.matcher(line);
                if (beginsShiftMatcher.find()) {
                    action = Action.BEGINS_SHIFT;
                    guardId = Integer.parseInt(beginsShiftMatcher.group(1));
                } else if (PATTERN_WAKES_UP.matcher(line).find())
                    action = Action.WAKES_UP;
                else if (PATTERN_FALLS_ASLEEP.matcher(line).find())
                    action = Action.FALLS_ASLEEP;
                else throw new AdventOfCodeRuntimeException("No action matched!");

                return new GuardLogEntry(extractTimestamp(line), action, guardId);
            }

            private static Instant extractTimestamp(String line) {
                final Matcher timestampMatcher = PATTERN_TIMESTAMP.matcher(line);
                if (!timestampMatcher.find())
                    throw new AdventOfCodeRuntimeException("Could not find timestamp");

                final Date timestamp;
                try {
                    timestamp = new SimpleDateFormat(FORMAT_TIMESTAMP).parse(timestampMatcher.group(1));
                } catch (ParseException e) {
                    throw new AdventOfCodeRuntimeException("Could not parse timestamp");
                }
                return timestamp.toInstant();
            }

            Instant getTimestamp() {
                return timestamp;
            }

            Action getAction() {
                return action;
            }

            Integer getGuardId() {
                return guardId;
            }

            enum Action {
                BEGINS_SHIFT, WAKES_UP, FALLS_ASLEEP
            }
        }

        /**
         * GuardShift
         */
        private static class GuardShift {
            private Integer guardId;
            private final List<GuardNap> naps;

            GuardShift() {
                naps = new ArrayList<>();
            }

            static List<GuardShift> ofLogEntries(List<GuardLogEntry> logEntries) {
                final ArrayList<GuardShift> result = new ArrayList<>();

                GuardShift guardShift = null;
                Instant napStart = null;
                for (GuardLogEntry entry : logEntries) {
                    if (entry.getAction() == GuardLogEntry.Action.BEGINS_SHIFT) {
                        if (guardShift != null) {
                            result.add(guardShift);
                        }
                        napStart = null;
                        guardShift = new GuardShift();

                        guardShift.setGuardId(entry.getGuardId());
                    } else if (entry.getAction() == GuardLogEntry.Action.FALLS_ASLEEP) {
                        napStart = entry.getTimestamp();
                    } else if (entry.getAction() == GuardLogEntry.Action.WAKES_UP) {
                        if (napStart == null)
                            throw new AdventOfCodeRuntimeException("Woke up before falling asleep.");

                        Objects.requireNonNull(guardShift);
                        guardShift.getNaps().add(new GuardNap(napStart, entry.getTimestamp()));
                    } else {
                        throw new AdventOfCodeRuntimeException("No action matched.");
                    }
                }
                result.add(guardShift);

                return result;
            }

            Integer getGuardId() {
                return guardId;
            }

            void setGuardId(Integer guardId) {
                this.guardId = guardId;
            }

            List<GuardNap> getNaps() {
                return naps;
            }
        }

        /**
         * GuardNap
         */
        private static class GuardNap {
            private final Instant napStart;
            private final Instant napEnd;

            private GuardNap(Instant napStart, Instant napEnd) {
                this.napStart = napStart;
                this.napEnd = napEnd;
            }

            Instant getNapStart() {
                return napStart;
            }

            Instant getNapEnd() {
                return napEnd;
            }
        }
    }
}
