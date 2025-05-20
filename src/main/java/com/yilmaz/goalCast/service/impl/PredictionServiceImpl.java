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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PredictionServiceImpl.class);

    @Override
    public PredictionDto createPrediction(Long userId, PredictionCreateRequestDto dto) {
        // 1. User var mı?
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId)); // Hata mesajına ID eklemek iyi olur

        // 2. Match var mı?
        Match match = matchRepository.findById(dto.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + dto.getMatchId())); // Hata mesajına ID

        // 3. Maç bitmiş mi veya başlamış mı?
        if (match.isFinished()) { // Maç zaten bitmişse
            throw new BadRequestException("Cannot predict for a match that has already finished.");
        }
        if (match.getMatchDate().isBefore(LocalDateTime.now())) { // Maçın başlama tarihi geçmişse (yani başlamışsa)
            throw new BadRequestException("Cannot predict for a match that has already started.");
        }

        // 4. Zaten tahmin yapmış mı?
        boolean alreadyPredicted = predictionRepository
                .existsByUserIdAndMatchId(userId, match.getId());
        if (alreadyPredicted) {
            throw new BadRequestException("You have already made a prediction for this match.");
        }

        // 5. Yeterli puanı var mı?
        if (user.getTotalPoints() < dto.getStakePoints()) {
            throw new BadRequestException("You do not have enough points to make this prediction. Your points: " + user.getTotalPoints() + ", Stake: " + dto.getStakePoints());
        }
        // Stake puanı için minimum/maksimum kontrolü de eklenebilir (örn: 100-1000 arası)
        if (dto.getStakePoints() < 100 || dto.getStakePoints() > 1000) { // Varsayımsal limitler
            throw new BadRequestException("Stake points must be between 100 and 1000.");
        }


        // 6. Prediction kaydı oluştur
        Prediction prediction = new Prediction();
        prediction.setUser(user);
        prediction.setMatch(match);
        prediction.setPredictedHomeScore(dto.getPredictedHomeScore());
        prediction.setPredictedAwayScore(dto.getPredictedAwayScore());
        prediction.setStakePoints(dto.getStakePoints());
        prediction.setIsCorrect(null); // Sonuç açıklanınca doldurulacak
        prediction.setPointsWon(0);    // Başlangıçta 0, maç sonucuyla güncellenecek

        // 7. Kullanıcı puanını düşür
        user.setTotalPoints(user.getTotalPoints() - dto.getStakePoints());
        User updatedUser = userRepository.save(user); // Kullanıcının güncellenmiş puanını kaydet ve güncel User objesini al

        // 8. Tahmini kaydet
        Prediction savedPrediction = predictionRepository.save(prediction); // Tahmini kaydet

        // 9. DTO'yu oluştur ve güncel puanı ekle
        PredictionDto predictionDto = predictionMapper.toDto(savedPrediction);
        predictionDto.setUpdatedUserTotalPoints(updatedUser.getTotalPoints()); // Kullanıcının güncel puanını DTO'ya ekle

        logger.info("User {} created prediction for match {}. Staked: {}, Remaining points: {}",
                user.getUsername(), match.getId(), dto.getStakePoints(), updatedUser.getTotalPoints());

        return predictionDto;

    }

    @Override
    public List<PredictionDto> getUserPredictions(Long userId) {
        List<Prediction> preds = predictionRepository.findByUserId(userId);
        return preds.stream()
                .map(predictionMapper::toDto)
                .collect(Collectors.toList());
    }
}
