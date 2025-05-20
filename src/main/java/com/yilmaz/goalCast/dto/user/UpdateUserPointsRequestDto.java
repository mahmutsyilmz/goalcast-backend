package com.yilmaz.goalCast.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserPointsRequestDto {
    @NotNull(message = "Points adjustment amount cannot be null")
    private Integer pointsAdjustment;
}