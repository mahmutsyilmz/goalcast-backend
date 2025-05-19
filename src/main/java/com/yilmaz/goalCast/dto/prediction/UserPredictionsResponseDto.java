package com.yilmaz.goalCast.dto.prediction;

import lombok.Data;

import java.util.List;

@Data
public class UserPredictionsResponseDto {

    private List<PredictionDto> predictions;
    private int currentUserTotalPoints;
}
