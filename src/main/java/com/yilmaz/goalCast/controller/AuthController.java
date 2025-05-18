package com.yilmaz.goalCast.controller;

import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;
import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Void>> registerUser(@Valid @RequestBody RegistrationRequestDto registrationRequest){
        authService.registerUser(registrationRequest);
        ResponseDto<Void> response = new ResponseDto<>("User registered successfully", true, null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<AuthResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequest){
        AuthResponseDto response = authService.loginUser(loginRequest);
        return ResponseEntity.ok(new ResponseDto<>("Login successful", true, response));

    }
}
