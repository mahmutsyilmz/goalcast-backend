// src/main/java/com/yilmaz/goalCast/controller/user/MatchController.java
package com.yilmaz.goalCast.controller.user;

import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // MatchController.java
    @GetMapping
    public ResponseEntity<ResponseDto<List<MatchDto>>> getUpcomingMatches(
            @RequestParam(required = false) Long leagueId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        List<MatchDto> list = matchService.getUpcomingMatches(leagueId, startDate, endDate);
        return ResponseEntity.ok(new ResponseDto<>("Upcoming matches fetched", true, list));
    }

}
