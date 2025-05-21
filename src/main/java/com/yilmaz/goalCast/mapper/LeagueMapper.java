// src/main/java/com/yilmaz/goalCast/mapper/LeagueMapper.java
package com.yilmaz.goalCast.mapper;

import com.yilmaz.goalCast.dto.league.LeagueCreateRequestDto;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.league.LeagueUpdateRequestDto;
import com.yilmaz.goalCast.model.League;
import org.springframework.stereotype.Component;

@Component
public class LeagueMapper {

    public LeagueDto toDto(League league) {
        if (league == null) {
            return null;
        }
        LeagueDto dto = new LeagueDto();
        dto.setId(league.getId());
        dto.setName(league.getName());
        dto.setCountry(league.getCountry().name());
        dto.setLeagueType(league.getLeagueType().name());
        return dto;
    }

    public League toEntity(LeagueCreateRequestDto dto) {
        if (dto == null) {
            return null;
        }
        League league = new League();
        league.setName(dto.getName());
        league.setCountry(dto.getCountry());
        league.setLeagueType(dto.getLeagueType());
        return league;
    }

    public void updateEntityFromDto(LeagueUpdateRequestDto dto, League league) {
        if (dto == null || league == null) {
            return;
        }
        league.setName(dto.getName());
        league.setCountry(dto.getCountry());
        league.setLeagueType(dto.getLeagueType());
    }
}
