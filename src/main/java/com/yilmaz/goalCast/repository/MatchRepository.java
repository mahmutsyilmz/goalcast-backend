package com.yilmaz.goalCast.repository;

import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByIsFinishedFalseOrderByMatchDateAsc();
    boolean existsByLeagueAndHomeTeamAndAwayTeamAndMatchDate(
            League league, String homeTeam, String awayTeam, LocalDateTime matchDate
    );

    List<Match> findByIsFinishedFalseAndLeagueIdOrderByMatchDateAsc(Long leagueId);

    List<Match> findByIsFinishedFalseAndMatchDateBetweenOrderByMatchDateAsc(LocalDateTime start, LocalDateTime end);

    List<Match> findByIsFinishedFalseAndLeagueIdAndMatchDateBetweenOrderByMatchDateAsc(
            Long leagueId, LocalDateTime start, LocalDateTime end
    );



}
