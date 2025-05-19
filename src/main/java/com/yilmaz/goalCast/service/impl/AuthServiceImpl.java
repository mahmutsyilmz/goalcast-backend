package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.auth.LoginRequestDto;
import com.yilmaz.goalCast.dto.auth.RegistrationRequestDto;
import com.yilmaz.goalCast.dto.auth.AuthResponseDto;
import com.yilmaz.goalCast.exception.BadRequestException;
import com.yilmaz.goalCast.exception.MessagingException;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.model.Role;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.security.JwtProvider;
import com.yilmaz.goalCast.service.AuthService;
import com.yilmaz.goalCast.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class); // LOGGER TANIMLA

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final NotificationService notificationService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtProvider jwtProvider,
                           NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.notificationService = notificationService;
    }

    @Override
    public void registerUser(RegistrationRequestDto registrationRequest) {
        if (userRepository.existsByUsername(registrationRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        user.setRole(Role.USER);
        user.setTotalPoints(5000); // Varsayılan puan
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());

        try {
            notificationService.sendEmailVerificationRequest(savedUser);
        } catch (MessagingException e) {

            logger.warn("User {} registered, but failed to queue verification email: {}", savedUser.getUsername(), e.getMessage());

        } catch (Exception e) {

            logger.error("An unexpected error occurred after user {} registration while trying to send verification email: {}", savedUser.getUsername(), e.getMessage(), e);
        }
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
        response.setEmailVerified(user.isEmailVerified());
        response.setTotalPoints(user.getTotalPoints());
        return response;
    }

    @Override
    public void requestEmailVerification(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email for user " + username + " is already verified.");
        }
        try {
            notificationService.sendEmailVerificationRequest(user);
            logger.info("Verification email request queued for user: {}", username);
        } catch (MessagingException e) {
            logger.warn("Failed to queue verification email for user {}: {}", username, e.getMessage());
            throw new BadRequestException("Failed to send verification email. Please try again later.");
        }
    }


    @Override
    public User processEmailVerification(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new BadRequestException("Verification token cannot be empty.");
        }

        User user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired verification token."));

        if (user.isEmailVerified()) {
            logger.info("Email for user {} is already verified.", user.getEmail());
            return user;
        }

        if (user.getEmailVerificationTokenExpiryDate() != null &&
                user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.setEmailVerificationToken(null);
            user.setEmailVerificationTokenExpiryDate(null);
            userRepository.save(user);
            throw new ResourceNotFoundException("Verification token has expired. Please request a new verification email."); // ResourceNotFound veya BadRequest olabilir.
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiryDate(null);
        logger.info("Email verified successfully for user: {}", user.getUsername());
        return userRepository.save(user);
    }
}
