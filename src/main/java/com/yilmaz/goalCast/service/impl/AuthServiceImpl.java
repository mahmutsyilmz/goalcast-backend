package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;
import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import com.yilmaz.goalCast.exception.BadRequestException;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.model.Role;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.security.JwtProvider;
import com.yilmaz.goalCast.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;



    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtProvider jwtProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }
    
    public void registerUser(RegistrationRequestDto registrationRequest){
        if(userRepository.existsByUsername(registrationRequest.getUsername())){
            throw new BadRequestException("Username is already taken");
        }
        
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }

    public AuthResponseDto loginUser(LoginRequestDto loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        String token = jwtProvider.generateToken(authentication);

        // Kullanıcıyı al
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        AuthResponseDto response = new AuthResponseDto();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRole(user.getRole().name());
        response.setId(user.getId());
        return response;
    }
}
