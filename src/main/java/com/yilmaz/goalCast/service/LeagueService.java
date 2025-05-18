// src/main/java/com/yilmaz/goalCast/service/LeagueService.java
package com.yilmaz.goalCast.service;



import com.yilmaz.goalCast.dto.league.LeagueCreateRequestDto;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.league.LeagueUpdateRequestDto;

import java.util.List;

public interface LeagueService {
    List<LeagueDto> getAllLeagues();
    LeagueDto createLeague(LeagueCreateRequestDto dto);
    LeagueDto updateLeague(Long id, LeagueUpdateRequestDto dto);
    void deleteLeague(Long id);
}
