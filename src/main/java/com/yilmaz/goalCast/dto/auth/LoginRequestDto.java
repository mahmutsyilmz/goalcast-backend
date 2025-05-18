package com.yilmaz.goalCast.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    @Schema(example = "adesimas")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(example = "M.sami1415")
    private String password;
}
