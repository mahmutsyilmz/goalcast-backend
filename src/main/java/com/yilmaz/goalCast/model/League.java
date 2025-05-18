package com.yilmaz.goalCast.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "leagues")
@Getter
@Setter
public class League extends BaseEntity{

    private String name;

    @Enumerated(value = EnumType.STRING)
    private Country country;

}
