package com.example.myInst.validations;

import com.example.myInst.annotations.PasswordMatches;
import com.example.myInst.payload.request.SignupRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {

    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintValidatorContext) {
        SignupRequest userSignupRequest = (SignupRequest) object;
        return userSignupRequest.getPassword().equals(userSignupRequest.getConfirmPassword());
    }
}
