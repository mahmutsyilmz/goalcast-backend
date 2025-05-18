package com.yilmaz.goalCast.config;

import com.yilmaz.goalCast.model.Country;
import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.Match;
import com.yilmaz.goalCast.model.Role;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.LeagueRepository;
import com.yilmaz.goalCast.repository.MatchRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
public class Config {

    @Bean
    public CommandLineRunner createDefaultUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            LeagueRepository leagueRepository,
            MatchRepository matchRepository
    ) {
        return args -> {
            String defaultUsername = "adesimas";
            if (!userRepository.existsByUsername(defaultUsername)) {
                User user = new User();
                user.setUsername(defaultUsername);
                user.setEmail("mahmut14sami15@gmail.com");
                user.setPassword(passwordEncoder.encode("M.sami1415"));
                user.setRole(Role.USER);
                userRepository.save(user);
            }
            String adminUsername = "admin";
            if (!userRepository.existsByUsername(adminUsername)) {
                User user = new User();
                user.setUsername(adminUsername);
                user.setEmail("admin@gmail.com");
                user.setPassword(passwordEncoder.encode("M.sami1415"));
                user.setRole(Role.ADMIN);
                userRepository.save(user);
            }
            String leagueName = "Trendyol Süper Lig";
            League league = leagueRepository.findByName(leagueName);
            if (league == null){
                league = new League();
                league.setName(leagueName);
                league.setCountry(Country.TURKEY);
                league = leagueRepository.save(league);
            }
            String teamHome = "Galatasaray";
            String teamAway = "Fenerbahçe";
            LocalDateTime matchDate = LocalDateTime.now().plusDays(10);

            // Aynı maç daha önce eklenmiş mi kontrolü
            boolean matchExists = matchRepository.existsByLeagueAndHomeTeamAndAwayTeamAndMatchDate(
                    league, teamHome, teamAway, matchDate
            );
            if (!matchExists) {
                Match match = new Match();
                match.setLeague(league);
                match.setHomeTeam(teamHome);
                match.setAwayTeam(teamAway);
                match.setMatchDate(matchDate);
                match.setHomeScore(0);
                match.setAwayScore(0);
                match.setFinished(false);
                matchRepository.save(match);
            }
        };
    }
}
