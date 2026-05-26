package com.pillreminder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import org.springframework.transaction.annotation.Transactional;

import com.pillreminder.entity.EmergencyContact;
import com.pillreminder.entity.User;

public interface EmergencyContactRepository
extends JpaRepository<EmergencyContact,Long>{

    List<EmergencyContact>
    findByUser(User user);

    @Transactional

    @Modifying

    @Query(
        "DELETE FROM EmergencyContact ec WHERE ec.user = :user"
    )

    void deleteByUser(

            @Param("user")
            User user
    );
}