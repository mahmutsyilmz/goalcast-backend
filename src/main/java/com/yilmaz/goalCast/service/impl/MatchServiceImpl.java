// src/main/java/com/yilmaz/goalCast/service/impl/MatchServiceImpl.java
package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.match.MatchCreateRequestDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.match.MatchResultUpdateDto;
import com.yilmaz.goalCast.dto.match.MatchUpdateRequestDto;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.MatchMapper;
import com.yilmaz.goalCast.model.League;
import com.yilmaz.goalCast.model.Match;
import com.yilmaz.goalCast.model.Prediction;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.LeagueRepository;
import com.yilmaz.goalCast.repository.MatchRepository;
import com.yilmaz.goalCast.repository.PredictionRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final LeagueRepository leagueRepository;
    private final MatchMapper matchMapper;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;

    @Override
    public List<MatchDto> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MatchDto createMatch(MatchCreateRequestDto dto) {
        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + dto.getLeagueId()));
        Match match = matchMapper.toEntity(dto, league);
        Match saved = matchRepository.save(match);
        return matchMapper.toDto(saved);
    }

    @Override
    public MatchDto updateMatch(Long id, MatchUpdateRequestDto dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + dto.getLeagueId()));
        matchMapper.updateEntityFromDto(dto, league, match);
        Match updated = matchRepository.save(match);
        return matchMapper.toDto(updated);
    }

    @Override
    public void deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        matchRepository.delete(match);
    }

    @Override
    public MatchDto updateMatchResult(Long id, MatchResultUpdateDto dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        matchMapper.updateResultFromDto(dto, match);
        Match updated = matchRepository.save(match);

        // *** ÖNEMLİ: Tahminleri değerlendir ***
        processPredictionsForMatch(updated);

        return matchMapper.toDto(updated);
    }

    /**
     * Maç sonuçlanınca tüm tahminleri puanlandır.
     */
    private void processPredictionsForMatch(Match match) {
        List<Prediction> predictions = predictionRepository.findByMatchId(match.getId());
        for (Prediction prediction : predictions) {
            boolean correctScore = prediction.getPredictedHomeScore() == match.getHomeScore()
                    && prediction.getPredictedAwayScore() == match.getAwayScore();
            boolean correctResult =
                    (match.getHomeScore() - match.getAwayScore() == prediction.getPredictedHomeScore() - prediction.getPredictedAwayScore()) &&
                            ((match.getHomeScore() > match.getAwayScore() && prediction.getPredictedHomeScore() > prediction.getPredictedAwayScore())
                                    || (match.getHomeScore() < match.getAwayScore() && prediction.getPredictedHomeScore() < prediction.getPredictedAwayScore())
                                    || (match.getHomeScore() == match.getAwayScore() && prediction.getPredictedHomeScore() == prediction.getPredictedAwayScore()));

            int pointsWon = 0;
            if (correctScore) {
                pointsWon = prediction.getStakePoints() * 2;
            } else if (correctResult) {
                pointsWon = prediction.getStakePoints() / 2;
            }
            prediction.setIsCorrect(correctScore);
            prediction.setPointsWon(pointsWon);

            // Kullanıcıya kazandığı puanları ekle
            if (pointsWon > 0) {
                User user = prediction.getUser();
                user.setTotalPoints(user.getTotalPoints() + pointsWon);
                userRepository.save(user);
            }
        }
        predictionRepository.saveAll(predictions);
    }

    @Override
    public List<MatchDto> getUpcomingMatches(Long leagueId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Match> matches;

        if (leagueId != null && startDate != null && endDate != null) {
            matches = matchRepository.findByIsFinishedFalseAndLeagueIdAndMatchDateBetweenOrderByMatchDateAsc(
                    leagueId, startDate, endDate);
        } else if (leagueId != null) {
            matches = matchRepository.findByIsFinishedFalseAndLeagueIdOrderByMatchDateAsc(leagueId);
        } else if (startDate != null && endDate != null) {
            matches = matchRepository.findByIsFinishedFalseAndMatchDateBetweenOrderByMatchDateAsc(startDate, endDate);
        } else {
            matches = matchRepository.findByIsFinishedFalseOrderByMatchDateAsc();
        }

        return matches.stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

}
