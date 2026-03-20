package ru.ifmo.se.io.input.env;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnvVariableProvider {

    private final String envName;

    public String getFileName() {
        return System.getenv(envName);
    }
}
