package ru.ifmo.se.io.input.readers;

import java.io.IOException;

public interface Reader {

    String readLine() throws IOException;

    String getName();
}
