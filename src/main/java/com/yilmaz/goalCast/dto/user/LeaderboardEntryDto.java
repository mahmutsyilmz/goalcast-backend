package com.yilmaz.goalCast.dto.user;

import lombok.Data;

@Data
public class LeaderboardEntryDto {
    private int rank;
    private String username;
    private int totalPoints;

    public LeaderboardEntryDto(int rank, String username, int totalPoints){

        this.rank = rank;
        this.totalPoints = totalPoints;
        this.username = username;

    }
}
