package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.user.LeaderboardEntryDto;
import com.yilmaz.goalCast.dto.user.UserManagementDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.UserMapper;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.NotificationRepository;
import com.yilmaz.goalCast.repository.PredictionRepository;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PredictionRepository predictionRepository;
    private final NotificationRepository notificationRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Override
    public UserProfileDto getProfile(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    public List<LeaderboardEntryDto> getLeaderboard(int limit) {
        Pageable topN = PageRequest.of(0, limit);
        List<User> topUsers = userRepository.findByOrderByTotalPointsDesc(topN);

        List<LeaderboardEntryDto> leaderboard = new ArrayList<>();
        int rank = 1;
        for (User user : topUsers) {
            leaderboard.add(new LeaderboardEntryDto(rank++,user.getUsername(), user.getTotalPoints()));
        }
        return leaderboard;
    }

    //getUserById
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserManagementDto> getAllUsersForAdmin() {
        logger.info("Fetching all users for admin management.");
        return userRepository.findAll().stream()
                .map(userMapper::toUserManagementDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserManagementDto adjustUserPoints(Long userId, int pointsAdjustment) {
        User user = getUserById(userId);

        int currentPoints = user.getTotalPoints();
        int newPoints = currentPoints + pointsAdjustment;


        user.setTotalPoints(newPoints);
        User updatedUser = userRepository.save(user);

        logger.info("Admin adjusted points for user: {} (ID: {}). Old points: {}, Adjustment: {}, New points: {}",
                updatedUser.getUsername(), userId, currentPoints, pointsAdjustment, newPoints);
        return userMapper.toUserManagementDto(updatedUser);
    }

    @Override
    @Transactional // Veritabanında değişiklik yapacak
    public void deleteUserByAdmin(Long userId) {
        User user = getUserById(userId); // Kullanıcının varlığını kontrol et

        // Kullanıcıya ait tahminleri ve bildirimleri sil
        predictionRepository.deleteByUserId(userId);
        notificationRepository.deleteByUserId(userId);

        userRepository.delete(user);
        logger.info("User: {} (ID: {}) successfully deleted by admin.", user.getUsername(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserManagementDto getUserDetailsForAdmin(Long userId) {
        User user = getUserById(userId);


        logger.info("Fetching details for user: {} (ID: {}) for admin view.", user.getUsername(), userId);
        return userMapper.toUserManagementDto(user);
    }
}
