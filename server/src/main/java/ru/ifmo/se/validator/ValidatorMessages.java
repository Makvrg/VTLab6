package ru.ifmo.se.validator;

public class ValidatorMessages {

    private ValidatorMessages() {
    }

    public static final String ID_MUST_BE_MORE_ZERO =
            "Id транспорта должен быть больше нуля";
    public static final String ARGUMENT_ID_MUST_BE_MORE_ZERO =
            "Переданный аргумент id должен быть больше нуля";
    public static final String PARAMETER_ID_NOT_PASSED =
            "Не передан параметр id";

    public static final String PARAMETER_TYPE_NOT_PASSED =
            "Не передан тип транспорта";
    public static final String PARAMETER_TYPE_INCORRECT =
            "Переданный тип транспорта некорректен";

    public static final String NAME_MUST_BE_NON_BLANK =
            "Название транспорта не должно быть пустым";

    public static final String CREATE_DATE_FORMAT_EXC =
            "Дата и время сборки транспорта "
                    + "должны иметь формат дд-ММ-гггг ЧЧ:мм:сс";
    public static final String CREATE_DATE_MUST_BE_NOT_NULL =
            "Дата и время сборки транспорта не должны отсутствовать";

    public static final String COORDS_MUST_BE_NOT_NULL =
            "Координаты не должны отсутствовать";
    public static final String COORDS_MUST_BE_IN_FORMAT =
            "Координаты должны быть в формате x:y";
    public static final String X_COORD_MUST_BE_NOT_NULL =
            "Координата x должна быть введена";
    public static final String X_COORD_MUST_BE_INTEGER =
            "Координата x должна быть целым числом";
    public static final String X_COORD_MUST_BE_MORE_MIN =
            "Координата x должна быть больше -482";
    public static final String Y_COORD_MUST_BE_INTEGER =
            "Координата y должна быть целым числом";

    public static final String ENGINE_POWER_MUST_BE_MORE_ZERO =
            "Мощность двигателя должна быть больше нуля";
    public static final String ENGINE_POWER_MUST_BE_REAL_NUM =
            "Мощность двигателя должна быть вещественным числом";
    public static final String ABS_ENGINE_POWER_MUST_BE_LESS_MAX =
            "Мощность двигателя по модулю не должна превышать максимум числа в памяти";

    public static final String DISTANCE_TRAVELLED_MUST_BE_MORE_ZERO =
            "Пробег должен быть больше нуля";
    public static final String DISTANCE_TRAVELLED_MUST_BE_NOT_NULL =
            "Пробег не должен отсутствовать";
    public static final String DISTANCE_TRAVELLED_MUST_BE_REAL_NUM =
            "Пробег должен быть вещественным числом";
    public static final String ABS_DISTANCE_TRAVELLED_MUST_BE_LESS_MAX =
            "Пробег по модулю не должен превышать максимум числа в памяти";

    public static final String VEHICLE_TYPE_MUST_BE_NOT_NULL =
            "Тип транспорта должен быть указан";

    public static final String FUEL_TYPE_MUST_BE_NOT_NULL =
            "Тип топлива должен быть указан";

    public static final String PARAMETER_FILE_NAME_NOT_PASSED =
            "Не введено название файла";
}
