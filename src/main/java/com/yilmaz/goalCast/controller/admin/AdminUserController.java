// goalcast-backend/src/main/java/com/yilmaz/goalCast/controller/admin/AdminUserController.java
package com.yilmaz.goalCast.controller.admin;

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.dto.user.UpdateUserPointsRequestDto;
import com.yilmaz.goalCast.dto.user.UserManagementDto;      // Bu DTO'yu oluşturmuştuk
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    // 1. Tüm kullanıcıları listeleme
    @GetMapping
    public ResponseEntity<ResponseDto<List<UserManagementDto>>> getAllUsersForAdmin() {
        List<UserManagementDto> users = userService.getAllUsersForAdmin();
        return ResponseEntity.ok(new ResponseDto<>("All users fetched successfully for admin.", true, users));
    }

    // 2. Belirli bir kullanıcının puanını ayarlama
    @PostMapping("/{userId}/adjust-points")
    public ResponseEntity<ResponseDto<UserManagementDto>> adjustUserPoints(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserPointsRequestDto requestDto) {

        UserManagementDto updatedUser = userService.adjustUserPoints(userId, requestDto.getPointsAdjustment());
        String message = String.format("Points for user '%s' (ID: %d) adjusted by %d. New total points: %d",
                updatedUser.getUsername(), userId, requestDto.getPointsAdjustment(), updatedUser.getTotalPoints());
        return ResponseEntity.ok(new ResponseDto<>(message, true, updatedUser));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDto<UserManagementDto>> getUserDetailsForAdmin(@PathVariable Long userId) {
        UserManagementDto userDto = userService.getUserDetailsForAdmin(userId);
        return ResponseEntity.ok(new ResponseDto<>(String.format("Details for user ID %d fetched.", userId), true, userDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDto<Void>> deleteUserByAdmin(@PathVariable Long userId) {
        userService.deleteUserByAdmin(userId);
        return ResponseEntity.ok(new ResponseDto<>(String.format("User with ID %d has been successfully deleted.", userId), true, null));
    }




}