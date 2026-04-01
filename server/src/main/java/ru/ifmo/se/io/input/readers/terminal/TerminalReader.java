package ru.ifmo.se.io.input.readers.terminal;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.io.input.readers.Reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class TerminalReader implements Reader {

    private final String name;
    private final BufferedReader bufferedReader =
            new BufferedReader(
                    new InputStreamReader(
                            System.in,
                            StandardCharsets.UTF_8
                    )
            );

    @Override
    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    @Override
    public String getName() {
        return name;
    }
}
