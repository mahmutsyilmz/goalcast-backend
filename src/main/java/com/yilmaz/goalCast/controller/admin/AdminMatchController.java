package com.yilmaz.goalCast.controller.admin;

import com.yilmaz.goalCast.dto.*;
import com.yilmaz.goalCast.dto.match.MatchCreateRequestDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.match.MatchResultUpdateDto;
import com.yilmaz.goalCast.dto.match.MatchUpdateRequestDto;
import com.yilmaz.goalCast.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/matches")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMatchController {

    private final MatchService matchService;

    @PostMapping
    public ResponseEntity<ResponseDto<MatchDto>> create(
            @Valid @RequestBody MatchCreateRequestDto dto) {
        MatchDto created = matchService.createMatch(dto);
        return ResponseEntity.ok(new ResponseDto<>("Match created", true, created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<MatchDto>> update(
            @PathVariable Long id,
            @Valid @RequestBody MatchUpdateRequestDto dto) {
        MatchDto updated = matchService.updateMatch(id, dto);
        return ResponseEntity.ok(new ResponseDto<>("Match updated", true, updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Void>> delete(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.ok(new ResponseDto<>("Match deleted", true, null));
    }

    @PutMapping("/{id}/result")
    public ResponseEntity<ResponseDto<MatchDto>> updateResult(
            @PathVariable Long id,
            @Valid @RequestBody MatchResultUpdateDto dto) {
        MatchDto updated = matchService.updateMatchResult(id, dto);
        return ResponseEntity.ok(new ResponseDto<>("Result updated", true, updated));
    }
}
