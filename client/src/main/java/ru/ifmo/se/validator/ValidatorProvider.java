package ru.ifmo.se.validator;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class ValidatorProvider {

    private static final Validator BEAN_VALIDATOR;
    private static final DataValidator DATA_VALIDATOR;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        BEAN_VALIDATOR = factory.getValidator();
        factory.close();

        DATA_VALIDATOR = new DataValidator();
    }

    public Validator getBeanValidator() {
        return BEAN_VALIDATOR;
    }

    public DataValidator getDataValidator() {
        return DATA_VALIDATOR;
    }
}
