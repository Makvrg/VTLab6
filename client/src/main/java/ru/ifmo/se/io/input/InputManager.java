package ru.ifmo.se.io.input;

import lombok.Getter;
import ru.ifmo.se.io.input.exceptions.EndOfFileException;
import ru.ifmo.se.io.input.exceptions.IORuntimeException;
import ru.ifmo.se.io.input.readers.InputTextHandler;
import ru.ifmo.se.io.input.readers.Reader;
import ru.ifmo.se.io.input.readers.file.FileReader;
import ru.ifmo.se.io.input.readers.terminal.TerminalReader;
import ru.ifmo.se.io.output.formatter.StringFormatter;
import ru.ifmo.se.io.output.print.Printer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputManager {

    @Getter
    private final List<Reader> readers = new ArrayList<>();

    private final Printer printer;
    private final StringFormatter formatter;

    public InputManager(Reader reader,
                        Printer printer,
                        StringFormatter formatter) {
        this.readers.add(reader);
        this.printer = printer;
        this.formatter = formatter;
    }

    public String[] readInput() {
        printer.printIfOn("> ");
        Reader currentReader = readers.get(readers.size() - 1);
        try {
            String inputLine = currentReader.readLine();

            if (inputLine == null) {
                if (currentReader instanceof FileReader) {
                    removeCurrentReader();
                    throw new EndOfFileException();
                }
                inputLine = "";
                printer.forcePrintln("");
            }

            return InputTextHandler.parseWords(
                    inputLine
            );
        } catch (IOException e) {
            handleReadException(currentReader);
            throw new IORuntimeException();
        }
    }

    private void handleReadException(Reader currentReader) {
        if (currentReader instanceof TerminalReader) {
            printer.forcePrintln(
                    "При чтении с терминала произошла ошибка"
            );
        }
        if (currentReader instanceof FileReader) {
            printer.forcePrintln(
                    "Файл с указанным названием не найден или к нему нет доступа"
            );
            removeCurrentReader();
        }
    }

    private void removeCurrentReader() {
        readers.remove(readers.size() - 1);

        if (readers.size() == 1) {
            printer.on();
        }

        printer.forcePrintln(
                formatter.formatCurrentReaderInfo(List.copyOf(readers))
        );
    }
}
