package ru.ifmo.se.io.input.fileprovider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileProvider implements DataProvider {

    @Override
    public InputStreamReader openStreamReader(String fileName) throws IOException {
        return new InputStreamReader(new FileInputStream(fileName),
                StandardCharsets.UTF_8
        );
    }
}
