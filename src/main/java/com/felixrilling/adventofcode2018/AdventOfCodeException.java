package com.felixrilling.adventofcode2018;

public class AdventOfCodeException extends Exception {
    public AdventOfCodeException(String message) {
        super(message);
    }

    public AdventOfCodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdventOfCodeException(Throwable cause) {
        super(cause);
    }
}
