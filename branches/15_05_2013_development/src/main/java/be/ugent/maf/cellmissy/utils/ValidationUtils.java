/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.ugent.maf.cellmissy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * Utilities class for entities validation.
 * @author Paola Masuzzo
 */
public class ValidationUtils {

    public static <T> List<String> validateObject(T t) {
        List<String> messages = new ArrayList<>();
        ValidatorFactory entityValidator = Validation.buildDefaultValidatorFactory();
        Validator validator = entityValidator.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        if (!constraintViolations.isEmpty()) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                messages.add(constraintViolation.getMessage());
            }
        }
        return messages;
    }

}
