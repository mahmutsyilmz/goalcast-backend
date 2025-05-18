package com.yilmaz.goalCast.service;



import com.yilmaz.goalCast.dto.prediction.PredictionCreateRequestDto;
import com.yilmaz.goalCast.dto.prediction.PredictionDto;

import java.util.List;

public interface PredictionService {
    PredictionDto createPrediction(Long userId, PredictionCreateRequestDto dto);
    List<PredictionDto> getUserPredictions(Long userId);
}
