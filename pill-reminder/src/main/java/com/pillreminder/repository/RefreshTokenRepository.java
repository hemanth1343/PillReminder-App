package com.pillreminder.repository;

import com.pillreminder.entity.RefreshToken;
import com.pillreminder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUser(User user);

	@Transactional
	@Modifying
	@Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
	void deleteByUser(@Param("user") User user);
}