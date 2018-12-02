package com.felixrilling.adventofcode2018;

public class AdventOfCodeRuntimeException extends RuntimeException {
    public AdventOfCodeRuntimeException(String message) {
        super(message);
    }

    public AdventOfCodeRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AdventOfCodeRuntimeException(Throwable cause) {
        super(cause);
    }
}
