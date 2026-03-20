package ru.ifmo.se.io.output.translator;

import java.util.HashMap;
import java.util.Map;

public class Translator {

    private Translator() {
    }

    private static final Map<String, String> TRANSLATE_TO_RUS_MAP = new HashMap<>();
    private static final Map<String, String> TRANSLATE_TO_ENG_MAP = new HashMap<>();

    static {
        TRANSLATE_TO_RUS_MAP.put("ELECTRICITY", "Электричество");
        TRANSLATE_TO_RUS_MAP.put("NUCLEAR", "Ядерная энергия");
        TRANSLATE_TO_RUS_MAP.put("PLASMA", "Плазма");

        TRANSLATE_TO_RUS_MAP.put("BICYCLE", "Велосипед");
        TRANSLATE_TO_RUS_MAP.put("MOTORCYCLE", "Мотоцикл");
        TRANSLATE_TO_RUS_MAP.put("CHOPPER", "Чоппер");
        TRANSLATE_TO_RUS_MAP.put("HOVERBOARD", "Ховерборд");

        TRANSLATE_TO_ENG_MAP.put("Электричество", "ELECTRICITY");
        TRANSLATE_TO_ENG_MAP.put("Ядерная энергия", "NUCLEAR");
        TRANSLATE_TO_ENG_MAP.put("Плазма", "PLASMA");

        TRANSLATE_TO_ENG_MAP.put("Велосипед", "BICYCLE");
        TRANSLATE_TO_ENG_MAP.put("Мотоцикл", "MOTORCYCLE");
        TRANSLATE_TO_ENG_MAP.put("Чоппер", "CHOPPER");
        TRANSLATE_TO_ENG_MAP.put("Ховерборд", "HOVERBOARD");
    }

    public static String translateToRusOrSelf(String engString) {
        return TRANSLATE_TO_RUS_MAP.getOrDefault(engString, engString);
    }

    public static String translateToEngOrSelf(String rusString) {
        return TRANSLATE_TO_ENG_MAP.getOrDefault(rusString, rusString);
    }
}
