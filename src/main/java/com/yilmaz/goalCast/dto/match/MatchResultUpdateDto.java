// src/main/java/com/yilmaz/goalCast/dto/MatchResultUpdateDto.java
package com.yilmaz.goalCast.dto.match;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MatchResultUpdateDto {

    @NotNull(message = "Home score is required")
    @Min(value = 0, message = "Score must be zero or positive")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    @Min(value = 0, message = "Score must be zero or positive")
    private Integer awayScore;
}
