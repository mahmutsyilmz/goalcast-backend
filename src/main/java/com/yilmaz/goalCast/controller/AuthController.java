package com.yilmaz.goalCast.controller.auth; // Paket adını kendi yapına göre ayarla (auth veya user olabilir)

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.security.CustomUserDetails;
import com.yilmaz.goalCast.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;
import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDto<Void>> registerUser(@Valid @RequestBody RegistrationRequestDto registrationRequest) {

        authService.registerUser(registrationRequest);

        return ResponseEntity.ok(new ResponseDto<>("User registered successfully. Please check your email to verify your account.", true, null));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<AuthResponseDto>> loginUser(@Valid @RequestBody LoginRequestDto loginRequest) {

        AuthResponseDto authResponse = authService.loginUser(loginRequest);

        return ResponseEntity.ok(new ResponseDto<>("User logged in successfully", true, authResponse));
    }

    @PostMapping("/request-verification-email")
    public ResponseEntity<ResponseDto<Void>> requestVerificationEmail(
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        authService.requestEmailVerification(currentUser.getUsername());
        return ResponseEntity.ok(new ResponseDto<>("Verification email request sent. Please check your email.", true, null));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<ResponseDto<Void>> verifyEmail(@RequestParam("token") String token) {
        authService.processEmailVerification(token);

        return ResponseEntity.ok(new ResponseDto<>("Email verified successfully. You can now log in.", true, null));
    }
}