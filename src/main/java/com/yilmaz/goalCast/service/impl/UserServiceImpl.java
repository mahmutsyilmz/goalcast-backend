package com.yilmaz.goalCast.service.impl;

import com.yilmaz.goalCast.dto.user.LeaderboardEntryDto;
import com.yilmaz.goalCast.dto.user.UserProfileDto;
import com.yilmaz.goalCast.exception.ResourceNotFoundException;
import com.yilmaz.goalCast.mapper.UserMapper;
import com.yilmaz.goalCast.model.User;
import com.yilmaz.goalCast.repository.UserRepository;
import com.yilmaz.goalCast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
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
}
