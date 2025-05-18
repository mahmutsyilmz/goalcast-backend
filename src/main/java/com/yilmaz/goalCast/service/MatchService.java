// src/main/java/com/yilmaz/goalCast/service/MatchService.java
package com.yilmaz.goalCast.service;

import com.yilmaz.goalCast.dto.match.MatchCreateRequestDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.match.MatchResultUpdateDto;
import com.yilmaz.goalCast.dto.match.MatchUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchService {

    // Yeni eklenen: sadece bitmemiş maçlar
    List<MatchDto> getUpcomingMatches(Long leagueId, LocalDateTime startDate, LocalDateTime endDate);
    List<MatchDto> getAllMatches();
    MatchDto createMatch(MatchCreateRequestDto dto);
    MatchDto updateMatch(Long id, MatchUpdateRequestDto dto);
    void deleteMatch(Long id);
    MatchDto updateMatchResult(Long id, MatchResultUpdateDto dto);
}
