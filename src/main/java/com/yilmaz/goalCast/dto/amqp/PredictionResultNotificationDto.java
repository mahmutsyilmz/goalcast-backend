package com.yilmaz.goalCast.dto.amqp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PredictionResultNotificationDto {
    private Long userId; // Bildirimin gönderileceği kullanıcı
    private Long matchId;
    private String homeTeam;
    private String awayTeam;
    private int actualHomeScore;
    private int actualAwayScore;
    private int predictedHomeScore;
    private int predictedAwayScore;
    private int pointsStake;
    private int pointsWon; // Kazanılan veya kaybedilen (negatif olabilir) puan
    private boolean isCompletelyCorrect; // Skoru tam bildi mi?
    private boolean isOutcomeCorrect; // Maç sonucunu (galibiyet/beraberlik/mağlubiyet) bildi mi?



}