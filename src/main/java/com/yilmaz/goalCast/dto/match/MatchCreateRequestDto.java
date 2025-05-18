package com.yilmaz.goalCast.dto.match;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MatchCreateRequestDto {

    @NotNull(message = "League ID is required")
    private Long leagueId;

    @NotBlank(message = "Home team is required")
    private String homeTeam;

    @NotBlank(message = "Away team is required")
    private String awayTeam;

    @NotNull(message = "Match date is required")
    @FutureOrPresent(message = "Match date must be in the future")
    private LocalDateTime matchDate;
}
