package com.pillreminder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.pillreminder.entity.EmailOtp;

import jakarta.transaction.Transactional;

@Repository
public interface EmailOtpRepository
        extends JpaRepository<EmailOtp, Long> {

    Optional<EmailOtp> findByEmail(String email);

    @Transactional
    @Modifying
    void deleteByEmail(String email);
}
