package com.medicare.repository;

import com.medicare.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUserEmail(String userEmail);
    Optional<RefreshToken> findByUserEmail(String userEmail);
}
