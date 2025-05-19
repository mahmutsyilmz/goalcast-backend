package com.yilmaz.goalCast.dto.amqp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewMatchNotificationDto {
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime matchDate;
    private String leagueName;
    private Long leagueId;
    private boolean sendEmailToAll = false;



}