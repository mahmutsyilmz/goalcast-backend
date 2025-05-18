package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;

public interface AuthService {
    void registerUser(RegistrationRequestDto registrationRequestDto);
    AuthResponseDto loginUser(LoginRequestDto loginRequestDto);

}
