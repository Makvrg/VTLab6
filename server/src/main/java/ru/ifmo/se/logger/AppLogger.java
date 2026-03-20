package ru.ifmo.se.logger;

import java.util.logging.Logger;

public final class AppLogger {

    private AppLogger() {}

    public static final Logger LOGGER = Logger.getLogger("server");
}
