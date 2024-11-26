package es.udc.ws.app.model.matchservice.validations;

import es.udc.ws.util.exceptions.InputValidationException;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomPropertyValidator {

    public static void validateFloat(String propertyName, float floatValue, float lowerValidLimit)
            throws InputValidationException {
        if (floatValue < lowerValidLimit) {
            throw new InputValidationException("Invalid " + propertyName +
                    " value (it must be greater than " + lowerValidLimit + "): " + floatValue);
        }
    }

    public static void validateInt(String fieldName, int value, int minValue)
            throws InputValidationException {
        if (value < minValue) {
            throw new InputValidationException(fieldName + " must be greater than or equal to " + minValue);
        }
    }

    public static void validateLocalDateTime(String fieldName, LocalDateTime dateTime) throws InputValidationException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if (dateTime == null) {
            throw new InputValidationException("Invalid " + fieldName + " value (it cannot be null): null");
        }else if (dateTime.isBefore(currentDateTime)) {
            throw new InputValidationException(fieldName + " It must be later than or equal to the current date.");
        }
    }

    public static void validateEmail(String email) throws InputValidationException {
        // Expresión regular para validar un formato de correo electrónico básico
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);

        if (email == null || email.trim().isEmpty()) {
            throw new InputValidationException("Invalid value (it cannot be null or empty): " + email);
        }

        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new InputValidationException("Invalid format: " + email);
        }
    }

}

