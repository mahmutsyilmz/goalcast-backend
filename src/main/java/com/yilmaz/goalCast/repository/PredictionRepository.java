// src/main/java/com/yilmaz/goalCast/repository/PredictionRepository.java
package com.yilmaz.goalCast.repository;

import com.yilmaz.goalCast.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    boolean existsByUserIdAndMatchId(Long userId, Long matchId);
    List<Prediction> findByUserId(Long userId);
    List<Prediction> findByMatchId(Long matchId);
}
