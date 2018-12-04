package com.felixrilling.adventofcode2018.day4;

import com.felixrilling.adventofcode2018.AdventOfCodeRuntimeException;
import com.felixrilling.adventofcode2018.AdventOfCodeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day4Part1 {
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
            int sleepiestGuardId = findSleepiestGuard(guardShifts);
            int sleepiestTime = findSleepiestTimeByGuard(sleepiestGuardId, guardShifts);

            return sleepiestGuardId * sleepiestTime;
        }

        private int findSleepiestGuard(List<GuardShift> guardShifts) {
            Map<Integer, Duration> guardSleepAmount = new HashMap<>();
            for (GuardShift shift : guardShifts) {
                Duration totalSleep = shift.getNaps().stream()
                    .map(nap -> Duration.between(nap.getNapStart(), nap.getNapEnd()))
                    .reduce(Duration::plus)
                    .orElse(Duration.ZERO);

                if (guardSleepAmount.containsKey(shift.getGuardId())) {
                    guardSleepAmount.put(shift.getGuardId(), guardSleepAmount.get(shift.getGuardId()).plus(totalSleep));
                } else guardSleepAmount.put(shift.getGuardId(), totalSleep);
            }

            return guardSleepAmount.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElseThrow().getKey();
        }

        private Integer findSleepiestTimeByGuard(int guardId, List<GuardShift> guardShifts) {
            List<GuardShift> sleepiestGuardShifts = guardShifts.stream()
                .filter(shift -> shift.getGuardId() == guardId)
                .collect(Collectors.toList());

            Map<Integer, Integer> minutesBySleep = new HashMap<>();
            for (GuardShift shift : sleepiestGuardShifts) {
                for (GuardNap nap : shift.getNaps()) {
                    int napStartMin = Date.from(nap.getNapStart()).getMinutes();
                    int napEndMin = Date.from(nap.getNapEnd()).getMinutes();

                    for (int minute = 0; minute < 60; minute++) {
                        if (minute >= napStartMin && minute < napEndMin)
                            minutesBySleep.put(minute, minutesBySleep.containsKey(minute) ? minutesBySleep.get(minute) + 1 : 1);
                    }
                }
            }

            return minutesBySleep.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElseThrow().getKey();
        }
    }

    /**
     * GuardLogEntry
     */
    private static class GuardLogEntry {
        private final Instant timestamp;
        private final Action action;
        private final Integer guardId;

        enum Action {
            BEGINS_SHIFT, WAKES_UP, FALLS_ASLEEP
        }

        private static final Pattern PATTERN_BEGINS_SHIFT = Pattern.compile("Guard #(.+) begins shift");
        private static final Pattern PATTERN_WAKES_UP = Pattern.compile("wakes up");
        private static final Pattern PATTERN_FALLS_ASLEEP = Pattern.compile("falls asleep");
        private static final Pattern PATTERN_TIMESTAMP = Pattern.compile("\\[(.+)]");
        private static final String FORMAT_TIMESTAMP = "yyyy-MM-dd kk:mm";

        GuardLogEntry(Instant timestamp, Action action, Integer guardId) {
            this.timestamp = timestamp;
            this.action = action;
            this.guardId = guardId;
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
    }

    /**
     * GuardShift
     */
    private static class GuardShift {
        private Integer guardId;
        private Instant shiftStart;
        private Instant shiftEnd;
        private List<GuardNap> naps;

        GuardShift() {
            naps = new ArrayList<>();
        }

        Integer getGuardId() {
            return guardId;
        }

        void setGuardId(Integer guardId) {
            this.guardId = guardId;
        }

        public Instant getShiftStart() {
            return shiftStart;
        }

        void setShiftStart(Instant shiftStart) {
            this.shiftStart = shiftStart;
        }

        public Instant getShiftEnd() {
            return shiftEnd;
        }

        void setShiftEnd(Instant shiftEnd) {
            this.shiftEnd = shiftEnd;
        }

        public List<GuardNap> getNaps() {
            return naps;
        }

        static List<GuardShift> ofLogEntries(List<GuardLogEntry> logEntries) {
            final ArrayList<GuardShift> result = new ArrayList<>();

            GuardShift guardShift = null;
            Instant napStart = null;
            for (GuardLogEntry entry : logEntries) {
                if (entry.getAction() == GuardLogEntry.Action.BEGINS_SHIFT) {
                    if (guardShift != null) {
                        guardShift.setShiftEnd(entry.getTimestamp());
                        result.add(guardShift);
                    }
                    napStart = null;
                    guardShift = new GuardShift();

                    guardShift.setGuardId(entry.getGuardId());
                    guardShift.setShiftStart(entry.getTimestamp());
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
