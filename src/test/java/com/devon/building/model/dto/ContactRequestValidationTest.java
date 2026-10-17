package com.devon.building.model.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContactRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsTenDigitPhoneStartingWithZero() {
        ContactRequest request = requestWithPhone("0123456789");

        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsPhoneThatIsNotTenDigitsStartingWithZero() {
        ContactRequest request = requestWithPhone("123456789");

        assertThat(validator.validate(request))
                .anySatisfy(violation -> {
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("phone");
                    assertThat(violation.getMessage())
                            .isEqualTo("Số điện thoại phải gồm 10 chữ số, bắt đầu bằng 0");
                });
    }

    private ContactRequest requestWithPhone(String phone) {
        ContactRequest request = new ContactRequest();
        request.setFullName("Nguyen Van A");
        request.setPhone(phone);
        return request;
    }
}
