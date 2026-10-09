package com.devon.building.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO extends AbstractDTO{

    @Serial
    private static final long serialVersionUID = 8835146939192307340L;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

}
