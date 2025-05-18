package com.yilmaz.goalCast.dto.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MatchDto {
    private Long id;
    private LeagueDto league;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime matchDate;
    private Integer homeScore;
    private Integer awayScore;
    private boolean isFinished;
}
