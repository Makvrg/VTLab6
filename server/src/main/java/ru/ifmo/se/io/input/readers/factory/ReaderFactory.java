package ru.ifmo.se.io.input.readers.factory;

import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.file.DataProvider;
import ru.ifmo.se.io.input.readers.file.FileReader;
import ru.ifmo.se.io.input.readers.terminal.TerminalReader;

import java.io.File;
import java.io.IOException;

public class ReaderFactory {

    public Reader createFileReader(String fileName,
                                   DataProvider dataProvider) {
        String canonicalFileName;
        try {
            canonicalFileName = getCanonicalPath(fileName);
        } catch (IOException e) {
            throw new ReaderCreateException(e.getMessage());
        }
        return new FileReader(
                canonicalFileName,
                dataProvider,
                fileName
        );
    }

    public Reader createTerminalReader(String terminalName) {
        return new TerminalReader(terminalName);
    }

    private String getCanonicalPath(String fileName) throws IOException {
        return new File(fileName).getCanonicalPath();
    }
}
