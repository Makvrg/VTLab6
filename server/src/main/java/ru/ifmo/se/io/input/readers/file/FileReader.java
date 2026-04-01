package ru.ifmo.se.io.input.readers.file;

import lombok.RequiredArgsConstructor;
import ru.ifmo.se.io.input.readers.Reader;

import java.io.IOException;
import java.util.Scanner;

@RequiredArgsConstructor
public class FileReader implements Reader {

    private final String name;
    private final DataProvider dataProvider;
    private final String fileName;
    private Scanner scanner;

    @Override
    public String readLine() throws IOException {
        if (scanner == null) {
            scanner = dataProvider.openScanner(fileName);
        }
        if (!scanner.hasNextLine()) {
            scanner.close();
            return null;
        }

        return scanner.nextLine();
    }

    @Override
    public String getName() {
        return name;
    }
}
