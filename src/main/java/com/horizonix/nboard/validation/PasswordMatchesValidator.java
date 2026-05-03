package com.horizonix.nboard.validation;

import com.horizonix.nboard.entity.User;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        User user = (User) obj;
        // Allow validation to pass if either password or confirmPassword is null (transient field)
        if (user.getPassword() == null || user.getConfirmPassword() == null) {
            return user.getPassword() == null && user.getConfirmPassword() == null;
        }
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
