// src/main/java/com/yilmaz/goalCast/service/impl/PredictionServiceImpl.java
package com.yilmaz.goalCast.service.impl;


import com.yilmaz.goalCast.dto.prediction.PredictionCreateRequestDto;
import com.yilmaz.goalCast.dto.prediction.PredictionDto;
import com.yilmaz.goalCast.exception.BadRequestException;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.PredictionMapper;
import com.yilmaz.goalCast.model.Match;
import com.yilmaz.goalCast.model.Prediction;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.MatchRepository;
import com.yilmaz.goalCast.repository.PredictionRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final PredictionRepository predictionRepository;
    private final PredictionMapper predictionMapper;

    @Override
    public PredictionDto createPrediction(Long userId, PredictionCreateRequestDto dto) {
        // 1. User var mı?
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 2. Match var mı?
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        // 3. Maç bitmiş mi?
        if (match.isFinished() || match.getMatchDate().isBefore(LocalDateTime.now()))
            throw new BadRequestException("You cannot predict finished or started matches.");

        // 4. Zaten tahmin yapmış mı?
        boolean alreadyPredicted = predictionRepository
                .existsByUserIdAndMatchId(userId, match.getId());
        if (alreadyPredicted)
            throw new BadRequestException("You have already predicted this match.");

        // 5. Yeterli puanı var mı?
        if (user.getTotalPoints() < dto.getStakePoints())
            throw new BadRequestException("You do not have enough points.");

        // 6. Prediction kaydı oluştur
        Prediction prediction = new Prediction();
        prediction.setUser(user);
        prediction.setMatch(match);
        prediction.setPredictedHomeScore(dto.getPredictedHomeScore());
        prediction.setPredictedAwayScore(dto.getPredictedAwayScore());
        prediction.setStakePoints(dto.getStakePoints());
        prediction.setIsCorrect(null); // Sonuç açıklanınca doldurulacak
        prediction.setPointsWon(0);

        // 7. Kullanıcı puanını düşür
        user.setTotalPoints(user.getTotalPoints() - dto.getStakePoints());
        userRepository.save(user);

        Prediction saved = predictionRepository.save(prediction);

        return predictionMapper.toDto(saved);
    }

    @Override
    public List<PredictionDto> getUserPredictions(Long userId) {
        List<Prediction> preds = predictionRepository.findByUserId(userId);
        return preds.stream()
                .map(predictionMapper::toDto)
                .collect(Collectors.toList());
    }
}
