package com.yilmaz.goalCast.repository;

import com.yilmaz.goalCast.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findByOrderByTotalPointsDesc(Pageable pageable);
}
