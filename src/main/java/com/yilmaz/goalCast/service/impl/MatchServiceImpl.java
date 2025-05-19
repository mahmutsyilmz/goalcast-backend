package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.amqp.NewMatchNotificationDto;
import com.yilmaz.goalCast.dto.amqp.PredictionResultNotificationDto;
import com.yilmaz.goalCast.dto.match.MatchCreateRequestDto;
import com.yilmaz.goalCast.dto.match.MatchDto;
import com.yilmaz.goalCast.dto.match.MatchResultUpdateDto;
import com.yilmaz.goalCast.dto.match.MatchUpdateRequestDto;
import com.yilmaz.goalCast.exception.MessagingException; // ÖNEMLİ: Bunu yakalayacağız
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
import com.yilmaz.goalCast.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final Logger logger = LoggerFactory.getLogger(MatchServiceImpl.class);

    private final MatchRepository matchRepository;
    private final LeagueRepository leagueRepository;
    private final MatchMapper matchMapper;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public MatchServiceImpl(MatchRepository matchRepository,
                            LeagueRepository leagueRepository,
                            MatchMapper matchMapper,
                            PredictionRepository predictionRepository,
                            UserRepository userRepository,
                            NotificationService notificationService) {
        this.matchRepository = matchRepository;
        this.leagueRepository = leagueRepository;
        this.matchMapper = matchMapper;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<MatchDto> getAllMatches() {
        return matchRepository.findAll().stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MatchDto createMatch(MatchCreateRequestDto dto) {
        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + dto.getLeagueId()));
        Match match = matchMapper.toEntity(dto, league);
        Match savedMatch = matchRepository.save(match);
        logger.info("Match created successfully with id: {}", savedMatch.getId());

        try {
            NewMatchNotificationDto notificationDto = new NewMatchNotificationDto(
                    savedMatch.getId(), savedMatch.getHomeTeam(), savedMatch.getAwayTeam(),
                    savedMatch.getMatchDate(), savedMatch.getLeague().getName(), savedMatch.getLeague().getId(),
                    dto.isSendEmailNotification());
            notificationService.sendNewMatchAddedNotificationRequest(notificationDto);
        } catch (MessagingException e) {
            logger.warn("Match (id:{}) created, but failed to queue new match notification: {}", savedMatch.getId(), e.getMessage());
        } catch (Exception e) { // Diğer beklenmedik hatalar
            logger.error("Unexpected error while queuing new match notification for matchId {}: {}", savedMatch.getId(), e.getMessage(), e);
        }
        return matchMapper.toDto(savedMatch);
    }

    @Override
    @Transactional
    public MatchDto updateMatch(Long id, MatchUpdateRequestDto dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        League league = leagueRepository.findById(dto.getLeagueId())
                .orElseThrow(() -> new ResourceNotFoundException("League not found with id: " + dto.getLeagueId()));
        matchMapper.updateEntityFromDto(dto, league, match);
        Match updated = matchRepository.save(match);
        logger.info("Match (id:{}) updated successfully.", updated.getId());
        return matchMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        matchRepository.delete(match);
        logger.info("Match (id:{}) deleted successfully.", id);
    }

    @Override
    @Transactional
    public MatchDto updateMatchResult(Long id, MatchResultUpdateDto dto) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        if (match.isFinished()) {
            logger.warn("Attempted to update result for an already finished match (id:{}).", id);
        }

        matchMapper.updateResultFromDto(dto, match);
        match.setFinished(true);
        Match updatedMatch = matchRepository.save(match);
        logger.info("Match (id:{}) result updated successfully.", updatedMatch.getId());

        processPredictionsForMatch(updatedMatch);
        return matchMapper.toDto(updatedMatch);
    }



    private void processPredictionsForMatch(Match match) {
        List<Prediction> predictions = predictionRepository.findByMatchId(match.getId());
        if (predictions.isEmpty()) {
            logger.info("No predictions found for matchId: {}. Skipping prediction processing.", match.getId());
            return;
        }
        logger.info("Processing {} predictions for matchId: {}", predictions.size(), match.getId());

        for (Prediction prediction : predictions) {
            User user = prediction.getUser();
            if (user == null) {
                logger.warn("Prediction (id:{}) has no associated user. Skipping processing.", prediction.getId());
                continue;
            }

            int currentTotalPointsAfterStake = user.getTotalPoints(); // Bahis zaten düşülmüştü
            int stakedPoints = prediction.getStakePoints();

            boolean correctScore = prediction.getPredictedHomeScore() == match.getHomeScore()
                    && prediction.getPredictedAwayScore() == match.getAwayScore();

            boolean homeWonActual = match.getHomeScore() > match.getAwayScore();
            boolean awayWonActual = match.getAwayScore() > match.getHomeScore();
            boolean drawActual = match.getHomeScore() == match.getAwayScore();

            boolean homeWonPredicted = prediction.getPredictedHomeScore() > prediction.getPredictedAwayScore();
            boolean awayWonPredicted = prediction.getPredictedAwayScore() > prediction.getPredictedHomeScore();
            boolean drawPredicted = prediction.getPredictedHomeScore() == prediction.getPredictedAwayScore();

            boolean correctOutcome = (homeWonActual && homeWonPredicted) ||
                    (awayWonActual && awayWonPredicted) ||
                    (drawActual && drawPredicted);

            int pointsAdjustmentToUserAccount; // Kullanıcının (düşülmüş) puanına eklenecek brüt miktar
            int netPointsWonForPredictionObject; // Prediction objesine kaydedilecek net kazanç/kayıp

            if (correctScore) {
                // Kullanıcı 1000 yatırdı, 3 katı (3000) hesabına brüt olarak eklenecek.
                pointsAdjustmentToUserAccount = stakedPoints * 3;
                netPointsWonForPredictionObject = stakedPoints * 2; // Net kazanç
                prediction.setIsCorrect(true); // Tam skoru bildiği için true
            } else if (correctOutcome) {
                // Kullanıcı 1000 yatırdı, 2 katı (2000) hesabına brüt olarak eklenecek.
                pointsAdjustmentToUserAccount = stakedPoints * 2;
                netPointsWonForPredictionObject = stakedPoints * 1; // Net kazanç
                prediction.setIsCorrect(false); // Skoru bilemedi ama sonucu bildi
            } else {
                // Yanlış sonuç, yatırdığını kaybetti. Puan zaten düşülmüştü.
                // Hesaba eklenecek brüt miktar 0.
                pointsAdjustmentToUserAccount = 0;
                netPointsWonForPredictionObject = -stakedPoints; // Net kayıp
                prediction.setIsCorrect(false);
            }

            prediction.setPointsWon(netPointsWonForPredictionObject); // Prediction objesine NET kazancı/kaybı kaydet
            user.setTotalPoints(currentTotalPointsAfterStake + pointsAdjustmentToUserAccount); // Kullanıcının puanını güncelle

            userRepository.save(user);
            logger.info("User {} (predictionId: {}): Staked: {}, Gross Adjustment to Account: {}, Final User Points: {}. Net for this pred: {}",
                    user.getUsername(), prediction.getId(), stakedPoints, pointsAdjustmentToUserAccount, user.getTotalPoints(), netPointsWonForPredictionObject);

            // --- Notification DTO Güncellemesi ---
            // DTO'ya hala netPointsWonForPredictionObject gönderiyoruz.
            // Frontend, "Kazanılan Puan" sütununu gösterirken bunu yorumlayacak.
            try {
                PredictionResultNotificationDto notificationDto = new PredictionResultNotificationDto(
                        user.getId(),
                        match.getId(),
                        match.getHomeTeam(),
                        match.getAwayTeam(),
                        match.getHomeScore(),
                        match.getAwayScore(),
                        prediction.getPredictedHomeScore(),
                        prediction.getPredictedAwayScore(),
                        stakedPoints,
                        netPointsWonForPredictionObject, // Net kazanç/kayıp
                        correctScore,                  // Skoru tam bildi mi?
                        correctOutcome                 // Sadece sonucu bildi mi?
                );
                notificationService.sendPredictionResultNotificationRequest(notificationDto);
            } catch (MessagingException e) {
                logger.warn("Prediction result processed for predictionId {}, but failed to queue notification for userId {}: {}",
                        prediction.getId(), user.getId(), e.getMessage());
            } catch (Exception e) {
                logger.error("Unexpected error while queuing prediction result notification for predictionId {}: {}", prediction.getId(), e.getMessage(), e);
            }
        }
        predictionRepository.saveAll(predictions);
        logger.info("All predictions processed for matchId: {}", match.getId());
    }


    @Override
    public List<MatchDto> getUpcomingMatches(Long leagueId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Match> matches;
        logger.debug("Fetching upcoming matches with leagueId: {}, startDate: {}, endDate: {}", leagueId, startDate, endDate);

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
        logger.info("Found {} upcoming matches matching criteria.", matches.size());
        return matches.stream()
                .map(matchMapper::toDto)
                .collect(Collectors.toList());
    }
}