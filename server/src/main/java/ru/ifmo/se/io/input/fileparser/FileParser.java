package ru.ifmo.se.io.input.fileparser;

import java.io.InputStreamReader;
import java.util.List;

public interface FileParser<T> {

    List<T> parse(InputStreamReader reader);
}
