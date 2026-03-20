package ru.ifmo.se.io.input.fileprovider;

import java.io.IOException;
import java.io.InputStreamReader;

public interface DataProvider {

    InputStreamReader openStreamReader(String fileName) throws IOException;
}
