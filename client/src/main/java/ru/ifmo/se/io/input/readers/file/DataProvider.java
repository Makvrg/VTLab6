package ru.ifmo.se.io.input.readers.file;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public interface DataProvider {

    Scanner openScanner(String fileName) throws IOException;

    InputStreamReader openStreamReader(String fileName) throws IOException;
}
