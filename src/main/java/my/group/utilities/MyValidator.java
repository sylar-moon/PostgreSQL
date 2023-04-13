package my.group.utilities;

import my.group.DTO.Good;
import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class MyValidator {
    private static final Logger LOGGER = new MyLogger().getLogger();
    public static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    public static final Validator VALIDATOR = FACTORY.getValidator();

    public boolean validateGood(Good good) {
        Set<ConstraintViolation<Good>> violation = VALIDATOR.validate(good);
        if (!violation.isEmpty()) {
            LOGGER.info("This good name is empty");
            return false;
        } else {
            return true;
        }
    }
}
