package my.group.Utilities;

import org.slf4j.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

public class MyValidator {
    private static final Logger LOGGER = new MyLogger().getLogger();
    public static final ValidatorFactory FACTORY = Validation.buildDefaultValidatorFactory();
    public static final Validator VALIDATOR = FACTORY.getValidator();

//    public Error validatePerson(Person person) {
//        Set<ConstraintViolation<Person>> violation = VALIDATOR.validate(person);
//        String[] result;
//        if (!violation.isEmpty()) {
//            int count = 0;
//            result = new String[violation.size()];
//            LOGGER.info("This Person is invalid:{}", person.getName());
//            for (ConstraintViolation<Person> viol : violation) {
//                result[count] = viol.getMessage();
//                count++;
//            }
//        } else {
//            LOGGER.info("This Person is VALID! :{}", person.getName());
//            return null;
//        }
//        return new Error(Arrays.toString(result));
//    }
}
