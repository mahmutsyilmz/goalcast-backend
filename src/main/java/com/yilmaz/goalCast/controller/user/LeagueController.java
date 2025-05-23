package com.yilmaz.goalCast.controller.user;

import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.service.LeagueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leagues")
@RequiredArgsConstructor
public class LeagueController {

    private final LeagueService leagueService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<LeagueDto>>> getAllLeagues() {
        List<LeagueDto> list = leagueService.getAllLeagues();
        return ResponseEntity.ok(new ResponseDto<>("Leagues fetched", true, list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<LeagueDto>> getLeagueById(@PathVariable Long id) {
        LeagueDto league = leagueService.getLeagueById(id);
        return ResponseEntity.ok(new ResponseDto<>("League fetched", true, league));
    }
}
