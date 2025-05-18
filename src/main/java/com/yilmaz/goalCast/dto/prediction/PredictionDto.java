package com.yilmaz.goalCast.dto.prediction;

import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import lombok.Data;

@Data
public class PredictionDto {
    private Long id;
    private UserProfileDto user;
    private MatchDto match;
    private int predictedHomeScore;
    private int predictedAwayScore;
    private int stakePoints;
    private Boolean isCorrect;
    private Integer pointsWon;
}
