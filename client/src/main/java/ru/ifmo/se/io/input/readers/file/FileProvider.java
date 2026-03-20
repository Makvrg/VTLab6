package ru.ifmo.se.io.input.readers.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileProvider implements DataProvider {

    @Override
    public Scanner openScanner(String fileName) throws IOException {
        return new Scanner(new File(fileName), StandardCharsets.UTF_8);
    }

    @Override
    public InputStreamReader openStreamReader(String fileName) throws IOException {
        return new InputStreamReader(new FileInputStream(fileName),
                StandardCharsets.UTF_8
        );
    }
}
