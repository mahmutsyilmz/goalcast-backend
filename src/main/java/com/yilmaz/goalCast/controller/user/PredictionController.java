// src/main/java/com/yilmaz/goalCast/controller/user/PredictionController.java
package com.yilmaz.goalCast.controller.user;

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.dto.prediction.PredictionCreateRequestDto;
import com.yilmaz.goalCast.dto.prediction.PredictionDto;
import com.yilmaz.goalCast.dto.prediction.UserPredictionsResponseDto;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.security.CustomUserDetails;
import com.yilmaz.goalCast.service.PredictionService;
import com.yilmaz.goalCast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDto<PredictionDto>> createPrediction(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody PredictionCreateRequestDto dto
    ) {
        Long userId = user.getUser().getId();
        PredictionDto prediction = predictionService.createPrediction(userId, dto);
        return ResponseEntity.ok(new ResponseDto<>("Prediction created", true, prediction));
    }

    @GetMapping("/user")
    public ResponseEntity<ResponseDto<UserPredictionsResponseDto>> getUserPredictions(@AuthenticationPrincipal CustomUserDetails currentUser) {
        User user = userService.getUserById(currentUser.getUser().getId()); // Güncel kullanıcıyı al
        List<PredictionDto> predictions = predictionService.getUserPredictions(currentUser.getUser().getId());

        UserPredictionsResponseDto responseDto = new UserPredictionsResponseDto();
        responseDto.setPredictions(predictions);
        responseDto.setCurrentUserTotalPoints(user.getTotalPoints());
        // responseDto.setCurrentUserEmailVerified(user.isEmailVerified());

        return ResponseEntity.ok(new ResponseDto<>("User predictions fetched", true, responseDto));
    }
}
