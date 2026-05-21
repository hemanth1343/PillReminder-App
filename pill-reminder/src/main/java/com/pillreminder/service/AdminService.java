package com.pillreminder.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pillreminder.dto.UserDto;
import com.pillreminder.entity.User;
import com.pillreminder.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /*
        GET ALL USERS
    */

    public List<UserDto> getAllUsers() {

        /*
            FETCH USERS FROM DATABASE
        */

        List<User> users =
                userRepository.findAll();

        /*
            DTO LIST
        */

        List<UserDto> dtoList =
                new ArrayList<>();

        /*
            CONVERT ENTITY TO DTO
        */

        for (User user : users) {

            UserDto dto =
                    new UserDto();

            /*
                SET ID
            */

            dto.setId(
                    user.getId()
            );

            /*
                SET FULL NAME
            */

            dto.setFullName(
                    user.getFullName()
            );

            /*
                SET EMAIL
            */

            dto.setEmail(
                    user.getEmail()
            );

            /*
                SET ROLE
            */

            if (user.getRole() != null) {

                dto.setRole(
                        user.getRole().name()
                );

            } else {

                dto.setRole("USER");
            }

            /*
                TOTAL MEDICATIONS
            */

            if (user.getMedications() != null) {

                dto.setTotalMedications(
                        user.getMedications().size()
                );

            } else {

                dto.setTotalMedications(0);
            }

            /*
                TOTAL REMINDERS
            */

            if (user.getReminderLogs() != null) {

                dto.setTotalReminders(
                        user.getReminderLogs().size()
                );

            } else {

                dto.setTotalReminders(0);
            }

            /*
                ADD DTO TO LIST
            */

            dtoList.add(dto);
        }

        /*
            RETURN DTO LIST
        */

        return dtoList;
    }
    
    @Transactional
    public void deleteUser(Long userId) {

        /*
            FIND USER
        */

        User user =
                userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        /*
            CLEAR CHILD RECORDS
        */

        if (user.getMedications() != null) {

            user.getMedications().clear();
        }

        if (user.getReminderLogs() != null) {

            user.getReminderLogs().clear();
        }

        /*
            DELETE USER
        */

        userRepository.delete(user);
    }
}