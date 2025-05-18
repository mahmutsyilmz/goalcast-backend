// src/main/java/com/yilmaz/goalCast/mapper/MatchMapper.java
package com.yilmaz.goalCast.mapper;

import com.yilmaz.goalCast.dto.match.MatchCreateRequestDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.match.MatchResultUpdateDto;
import com.yilmaz.goalCast.dto.match.MatchUpdateRequestDto;
import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    private final LeagueMapper leagueMapper;

    public MatchMapper(LeagueMapper leagueMapper) {
        this.leagueMapper = leagueMapper;
    }

    public MatchDto toDto(Match match) {
        if (match == null) return null;
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setLeague(leagueMapper.toDto(match.getLeague()));
        dto.setHomeTeam(match.getHomeTeam());
        dto.setAwayTeam(match.getAwayTeam());
        dto.setMatchDate(match.getMatchDate());
        dto.setHomeScore(match.getHomeScore());
        dto.setAwayScore(match.getAwayScore());
        dto.setFinished(match.isFinished());
        return dto;
    }

    public Match toEntity(MatchCreateRequestDto dto, League league) {
        Match match = new Match();
        match.setLeague(league);
        match.setHomeTeam(dto.getHomeTeam());
        match.setAwayTeam(dto.getAwayTeam());
        match.setMatchDate(dto.getMatchDate());
        // homeScore, awayScore default 0; isFinished default false
        return match;
    }

    public void updateEntityFromDto(MatchUpdateRequestDto dto, League league, Match match) {
        match.setLeague(league);
        match.setHomeTeam(dto.getHomeTeam());
        match.setAwayTeam(dto.getAwayTeam());
        match.setMatchDate(dto.getMatchDate());
        // scores/isFinished korunur
    }

    public void updateResultFromDto(MatchResultUpdateDto dto, Match match) {
        match.setHomeScore(dto.getHomeScore());
        match.setAwayScore(dto.getAwayScore());
        match.setFinished(true);
    }
}
