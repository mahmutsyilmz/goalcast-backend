package com.yilmaz.goalCast.repository;

import com.yilmaz.goalCast.model.League;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeagueRepository extends JpaRepository<League, Long> {
    boolean existsByName(String leagueName);
    League findByName(String leagueName);
}
