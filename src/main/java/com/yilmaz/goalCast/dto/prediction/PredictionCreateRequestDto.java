package com.yilmaz.goalCast.dto.prediction;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PredictionCreateRequestDto {

    @NotNull(message = "Match id is required")
    private Long matchId;

    @NotNull(message = "Predicted home score is required")
    @Min(0)
    private Integer predictedHomeScore;

    @NotNull(message = "Predicted away score is required")
    @Min(0)
    private Integer predictedAwayScore;

    @NotNull(message = "Stake points is required")
    @Min(value = 100, message = "Stake must be at least 100")
    @Max(value = 1000, message = "Stake must be at most 1000")
    private Integer stakePoints;
}
