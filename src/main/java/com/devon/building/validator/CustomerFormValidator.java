package com.devon.building.validator;

import com.devon.building.form.CustomerForm;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class CustomerFormValidator implements Validator {

   private EmailValidator emailValidator = new EmailValidator();

   @Override
   public boolean supports(Class<?> clazz) {
      return CustomerForm.class.isAssignableFrom(clazz);
   }

   @Override
   public void validate(Object target, Errors errors) {
      CustomerForm custInfo = (CustomerForm) target;

      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "NotEmpty.customerForm.name");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.customerForm.email");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "address", "NotEmpty.customerForm.address");
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phone", "NotEmpty.customerForm.phone");

      if (!errors.hasFieldErrors("email")) {
         if (!emailValidator.isValid(custInfo.getEmail(), null)) {
            errors.rejectValue("email", "Pattern.customerForm.email");
         }
      }
   }
}

