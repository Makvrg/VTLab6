package ru.ifmo.se.dto.entity.fieldmessages;

public class FieldMessages {

    private FieldMessages() {
    }

    public static final String ID_MUST_BE_MORE_ZERO =
            "Id транспорта должен быть больше нуля";

    public static final String NAME_MUST_BE_NON_BLANK =
            "Название транспорта не должно быть пустым";

    public static final String CREATE_DATE_MUST_BE_NOT_NULL =
            "Дата и время сборки транспорта не должны отсутствовать";

    public static final String COORDS_MUST_BE_NOT_NULL =
            "Координаты не должны отсутствовать";
    public static final String X_COORD_MUST_BE_NOT_NULL =
            "Координата x должна быть введена";
    public static final String X_COORD_MUST_BE_MORE_MIN =
            "Координата x должна быть больше -482";

    public static final String ENGINE_POWER_MUST_BE_MORE_ZERO =
            "Мощность двигателя должна быть больше нуля";

    public static final String DISTANCE_TRAVELLED_MUST_BE_MORE_ZERO =
            "Пробег должен быть больше нуля";
    public static final String DISTANCE_TRAVELLED_MUST_BE_NOT_NULL =
            "Пробег не должен отсутствовать";

    public static final String VEHICLE_TYPE_MUST_BE_NOT_NULL =
            "Тип транспорта должен быть указан";

    public static final String FUEL_TYPE_MUST_BE_NOT_NULL =
            "Тип топлива должен быть указан";
}
