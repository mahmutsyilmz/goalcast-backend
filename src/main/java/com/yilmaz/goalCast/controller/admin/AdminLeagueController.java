// src/main/java/com/yilmaz/goalCast/controller/admin/AdminLeagueController.java
package com.yilmaz.goalCast.controller.admin;

import com.yilmaz.goalCast.dto.league.LeagueCreateRequestDto;
import com.yilmaz.goalCast.dto.league.LeagueDto;
import com.yilmaz.goalCast.dto.league.LeagueUpdateRequestDto;
import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.service.LeagueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/leagues")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminLeagueController {

    private final LeagueService leagueService;

    @PostMapping
    public ResponseEntity<ResponseDto<LeagueDto>> createLeague(
            @Valid @RequestBody LeagueCreateRequestDto dto) {
        LeagueDto created = leagueService.createLeague(dto);
        return ResponseEntity.ok(new ResponseDto<>("League created", true, created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<LeagueDto>> updateLeague(
            @PathVariable Long id,
            @Valid @RequestBody LeagueUpdateRequestDto dto) {
        LeagueDto updated = leagueService.updateLeague(id, dto);
        return ResponseEntity.ok(new ResponseDto<>("League updated", true, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> deleteLeague(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.ok(new ResponseDto<>("League deleted", true, null));
    }
}
