package ru.ifmo.se.io.input.readers;

import java.util.Arrays;

public class InputTextHandler {

    private InputTextHandler() {
    }

    public static String[] parseWords(String line) {
        return line.strip().split("\\s+");
    }

    public static String parseCommandName(String[] input) {
        return (input.length > 0) ? input[0] : "";
    }

    public static String[] parseInputArgs(String[] input) {
        return (input.length > 0)
                ? Arrays.copyOfRange(input, 1, input.length)
                : input;
    }

    public static String stripOrNullField(String line) {
        if (line.isEmpty()) {
            return null;
        }
        return line.strip();
    }
}
