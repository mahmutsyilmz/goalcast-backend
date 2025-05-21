package com.yilmaz.goalCast.repository;

import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.Match;
import com.yilmaz.goalCast.model.LeagueType; // LeagueType import edildi
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByIsFinishedFalseOrderByMatchDateAsc();

    /**
     * Belirli bir lig, ev sahibi takım, deplasman takımı ve maç tarihi ile eşleşen bir maç olup olmadığını kontrol eder.
     * Not: Bu, genellikle yeni maç eklerken duplikasyon kontrolü için kullanılır.
     */
    boolean existsByLeagueAndHomeTeamAndAwayTeamAndMatchDate(
            League league, String homeTeam, String awayTeam, LocalDateTime matchDate
    );

    // --- Filtrelenmiş ve Henüz Bitmemiş Maçları Getirme ---

    // Sadece Lig ID'sine göre
    List<Match> findByIsFinishedFalseAndLeagueIdOrderByMatchDateAsc(Long leagueId);

    // Sadece Lig Türüne göre (Yeni)
    // League entity'si üzerinden leagueType'a ulaşacağız.
    List<Match> findByIsFinishedFalseAndLeague_LeagueTypeOrderByMatchDateAsc(LeagueType leagueType);

    // Sadece Tarih Aralığına göre
    List<Match> findByIsFinishedFalseAndMatchDateBetweenOrderByMatchDateAsc(LocalDateTime startDate, LocalDateTime endDate);

    // Lig ID'si VE Tarih Aralığına göre
    List<Match> findByIsFinishedFalseAndLeagueIdAndMatchDateBetweenOrderByMatchDateAsc(
            Long leagueId, LocalDateTime startDate, LocalDateTime endDate
    );

    // Lig Türü VE Tarih Aralığına göre
    List<Match> findByIsFinishedFalseAndLeague_LeagueTypeAndMatchDateBetweenOrderByMatchDateAsc(
            LeagueType leagueType, LocalDateTime startDate, LocalDateTime endDate
    );

    List<Match> findByIsFinishedFalseAndLeagueIdAndLeague_LeagueTypeOrderByMatchDateAsc(Long leagueId, LeagueType leagueType);


    // TÜM KRİTERLER: Lig ID'si, Lig Türü VE Tarih Aralığı (Yeni)
    // Bu en kapsamlı sorgu olabilir. Servis katmanı, hangi parametrelerin dolu olduğuna bakarak bu veya daha basit metotları çağırabilir.
    List<Match> findByIsFinishedFalseAndLeagueIdAndLeague_LeagueTypeAndMatchDateBetweenOrderByMatchDateAsc(
            Long leagueId, LeagueType leagueType, LocalDateTime startDate, LocalDateTime endDate
    );


}