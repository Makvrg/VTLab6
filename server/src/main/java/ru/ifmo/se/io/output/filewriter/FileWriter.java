package ru.ifmo.se.io.output.filewriter;

import java.io.IOException;
import java.util.Collection;

public interface FileWriter<T> {

    void write(String fileName, Collection<T> data) throws IOException;

    void writeBackup(Collection<T> data) throws IOException;
}
