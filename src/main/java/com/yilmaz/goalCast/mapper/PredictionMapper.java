// src/main/java/com/yilmaz/goalCast/mapper/PredictionMapper.java
package com.yilmaz.goalCast.mapper;

import com.yilmaz.goalCast.dto.prediction.PredictionDto;
import com.yilmaz.goalCast.model.Prediction;
import org.springframework.stereotype.Component;

@Component
public class PredictionMapper {
    private final UserMapper userMapper;
    private final MatchMapper matchMapper;

    public PredictionMapper(UserMapper userMapper, MatchMapper matchMapper) {
        this.userMapper = userMapper;
        this.matchMapper = matchMapper;
    }

    public PredictionDto toDto(Prediction pred) {
        if (pred == null) return null;
        PredictionDto dto = new PredictionDto();
        dto.setId(pred.getId());
        dto.setUser(userMapper.toDto(pred.getUser()));
        dto.setMatch(matchMapper.toDto(pred.getMatch()));
        dto.setPredictedHomeScore(pred.getPredictedHomeScore());
        dto.setPredictedAwayScore(pred.getPredictedAwayScore());
        dto.setStakePoints(pred.getStakePoints());
        dto.setIsCorrect(pred.getIsCorrect());
        dto.setPointsWon(pred.getPointsWon());
        return dto;
    }
}
