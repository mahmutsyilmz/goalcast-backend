package com.yilmaz.goalCast.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leagues")
@Getter
@Setter
public class League extends BaseEntity{

    private String name;

    @Enumerated(value = EnumType.STRING)
    private Country country;

    @Enumerated(EnumType.STRING)
    private LeagueType leagueType;

}
