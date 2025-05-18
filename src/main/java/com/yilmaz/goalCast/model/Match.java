package com.yilmaz.goalCast.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Match extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "league_id")
    private League league;

    private String homeTeam;

    private String awayTeam;

    private LocalDateTime matchDate;

    private int homeScore;

    private int awayScore;

    private boolean isFinished = false;
}
