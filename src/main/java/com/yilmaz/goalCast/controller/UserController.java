package com.yilmaz.goalCast.controller;

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.security.CustomUserDetails;
import com.yilmaz.goalCast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<ResponseDto<UserProfileDto>> getProfile (Authentication authentication){

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String username = principal.getUsername();

        UserProfileDto dto = userService.getProfile(username);
        ResponseDto<UserProfileDto> response =
                new ResponseDto<>("User profile fetched successfully",true, dto);

        return ResponseEntity.ok(response);

    }
}
