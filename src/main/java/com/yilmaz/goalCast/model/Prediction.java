package com.yilmaz.goalCast.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "predictions")
@Getter
@Setter
public class Prediction extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @ManyToOne
    @JoinColumn(name = "match_id",nullable = false)
    private Match match;

    private int predictedHomeScore;

    private int predictedAwayScore;

    private int stakePoints;

    private Boolean isCorrect;

    private Integer pointsWon = 0;
}
