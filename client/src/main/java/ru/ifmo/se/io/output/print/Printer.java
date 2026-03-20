package ru.ifmo.se.io.output.print;

public class Printer {

    private boolean onOff = true;

    public void printlnIfOn(String inputLine) {
        if (onOff) {
            System.out.println(inputLine);
        }
    }

    public void printIfOn(String inputLine) {
        if (onOff) {
            System.out.print(inputLine);
        }
    }

    public void forcePrintln(String inputLine) {
        System.out.println(inputLine);
    }

    public void forcePrint(String inputLine) {
        System.out.print(inputLine);
    }

    public void on() {
        onOff = true;
    }

    public void off() {
        onOff = false;
    }
}
