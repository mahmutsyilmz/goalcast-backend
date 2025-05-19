package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;
import com.yilmaz.goalCast.model.User;

public interface AuthService {
    void registerUser(RegistrationRequestDto registrationRequestDto);
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

    void requestEmailVerification(String username);

    User processEmailVerification(String token);

}
