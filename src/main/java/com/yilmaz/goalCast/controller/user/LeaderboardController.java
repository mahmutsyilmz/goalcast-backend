package com.yilmaz.goalCast.controller.user;

import com.yilmaz.goalCast.dto.ResponseDto;
import com.yilmaz.goalCast.dto.user.LeaderboardEntryDto;
import com.yilmaz.goalCast.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    private final UserService userService;

    @Autowired
    public LeaderboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<ResponseDto<List<LeaderboardEntryDto>>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit) { // Varsayılan olarak ilk 10 kişi
        try {
            List<LeaderboardEntryDto> leaderboard = userService.getLeaderboard(limit);
            return ResponseEntity.ok(new ResponseDto<>("Leaderboard fetched successfully", true, leaderboard));
        } catch (Exception e) {
            // Loglama eklenebilir
            return ResponseEntity.status(500).body(new ResponseDto<>("Error fetching leaderboard", false, null));
        }
    }
}

