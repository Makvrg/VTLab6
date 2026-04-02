package ru.ifmo.se.io.output;

public class CollectionActionsMessages {

    private CollectionActionsMessages() {
    }

    public static final String VEHICLE_INIT_VALID_EXC = """
            При инициализации коллекции данными из базы данных
            произошла ошибка валидации объекта Vehicle с id: %d
            """;
    public static final String VEHICLE_INIT_UNKNOWN_EXC = """
            При инициализации коллекции данными из базы данных
            по неизвестной причине не удалось добавить в коллекцию объект Vehicle с id: %d
            """;
    public static final String VEHICLE_INIT_ADD_EXC = """
            При инициализации коллекции данными из базы данных
            произошла ошибка добавления объекта Vehicle с id: %d
            """;
    public static final String VEHICLE_INIT_OPEN_FILE_EXC =
            "Произошла ошибка открытия файла при инициализации коллекции: ";
    public static final String SAVE_COLLECTION_EXC = """
            При сохранении коллекции в файл возникла ошибка, возможно, к нему
            нет прав и/или программа не может создать запасной файл для записи
            """;
}
