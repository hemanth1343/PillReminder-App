package com.pillreminder.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.config.JwtService;
import com.pillreminder.entity.EmailOtp;
import com.pillreminder.entity.Medication;
import com.pillreminder.entity.ReminderLog;
import com.pillreminder.entity.User;
import com.pillreminder.enums.Role;
import com.pillreminder.repository.EmergencyContactRepository;
import com.pillreminder.repository.MedicationRepository;
import com.pillreminder.repository.RefreshTokenRepository;
import com.pillreminder.repository.ReminderLogRepository;
import com.pillreminder.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    private final MedicationRepository medicationRepository;

    private final ReminderLogRepository reminderLogRepository;

    private final RefreshTokenRepository refreshTokenRepository;
    
    private final EmergencyContactRepository emergencyContactRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final JwtService jwtService;

    // SUMMARY

    @GetMapping("/stats/summary")
    public Map<String, Object> getSummary() {

        Map<String, Object> summary =
                new HashMap<>();

        List<User> users =
                userRepository.findAll();

        List<Medication> medications =
                medicationRepository.findAll();

        List<ReminderLog> logs =
                reminderLogRepository.findAll();

        summary.put(
                "totalUsers",
                users.size()
        );

        summary.put(
                "totalMedications",
                medications.size()
        );

        summary.put(
                "totalReminderLogs",
                logs.size()
        );

        return summary;
    }

    // USERS

    @GetMapping("/users")

    public ResponseEntity<?> getUsers(){

        List<User> users =
                userRepository.findAll();

        List<Map<String,Object>> response =
                new ArrayList<>();

        for(User user : users){

            Map<String,Object> map =
                    new HashMap<>();

            map.put(
                    "id",
                    user.getId()
            );

            map.put(
                    "fullName",
                    user.getFullName()
            );

            map.put(
                    "email",
                    user.getEmail()
            );

            map.put(
                    "role",
                    user.getRole()
            );

            map.put(
                    "enabled",
                    user.isEnabled()
            );

            map.put(
                    "blocked",
                    user.getBlocked()
            );

            response.add(map);
        }

        return ResponseEntity.ok(
                response
        );
    }

    // USER DETAILS

    @GetMapping("/users/{id}")

    public ResponseEntity<?> getUserDetails(
            @PathVariable Long id
    ){

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User Not Found"
                                ));

        Map<String,Object> response =
                new HashMap<>();

        response.put(
                "id",
                user.getId()
        );

        response.put(
                "fullName",
                user.getFullName()
        );

        response.put(
                "email",
                user.getEmail()
        );

        response.put(
                "role",
                user.getRole()
        );

        // MEDICATIONS

        List<Map<String,Object>> meds =
                new ArrayList<>();

        for(Medication med : user.getMedications()){

            Map<String,Object> medMap =
                    new HashMap<>();

            medMap.put(
                    "id",
                    med.getId()
            );

            medMap.put(
                    "medicationName",
                    med.getName()
            );

            meds.add(medMap);
        }

        // REMINDERS

        List<Map<String,Object>> reminders =
                new ArrayList<>();

        for(ReminderLog reminder :
            user.getReminderLogs()){

        Map<String,Object> remMap =
                new HashMap<>();

        remMap.put(
                "id",
                reminder.getId()
        );

        remMap.put(
                "status",
                reminder.getStatus()
        );

        remMap.put(
                "scheduledTime",
                reminder.getScheduledTime()
        );

        remMap.put(
                "notes",
                reminder.getNotes()
        );

        if(reminder.getMedication() != null){

            remMap.put(
                    "medicationName",
                    reminder.getMedication().getName()
            );
        }

        reminders.add(remMap);
    }

        response.put(
                "medications",
                meds
        );

        response.put(
                "reminderLogs",
                reminders
        );

        return ResponseEntity.ok(
                response
        );
    }

    // MEDICATIONS

    @GetMapping("/medications")
    public List<Medication> getMedications() {

        return medicationRepository.findAll();
    }

    // REMINDERS

    @GetMapping("/reminders")
    public List<ReminderLog> getReminders() {

        return reminderLogRepository.findAll();
    }

    // CREATE ADMIN

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(
            @RequestBody User request
    ) {

        boolean exists =
                userRepository.existsByEmail(
                        request.getEmail()
                );

        if (exists) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Email already exists"
                    );
        }

        User admin = new User();

        admin.setFullName(
                request.getFullName()
        );

        admin.setEmail(
                request.getEmail()
        );

        admin.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        admin.setPhone(
                request.getPhone()
        );

        admin.setTimezone(
                request.getTimezone()
        );

        admin.setRole(
                Role.ROLE_ADMIN
        );
        
        admin.setEnabled(true);

        userRepository.save(admin);

        return ResponseEntity.ok(
                "Admin created successfully"
        );
    }

    // DELETE USER
    @DeleteMapping("/users/{id}")

    @Transactional

    public ResponseEntity<?> deleteUser(

            @PathVariable Long id

    ){

        User user =
                userRepository.findById(id)
                .orElseThrow(() ->

                    new RuntimeException(
                        "User Not Found"
                    )
                );

        // DELETE TOKENS

        refreshTokenRepository
                .deleteByUser(user);

        // DELETE EMERGENCY CONTACTS

        emergencyContactRepository
                .deleteByUser(user);

        // DELETE USER

        userRepository.delete(user);

        return ResponseEntity.ok(
                "User Deleted Successfully"
        );
        
        
    }
    
 // BLOCK USER

    @PutMapping("/users/{id}/block")

    public ResponseEntity<String> blockUser(

            @PathVariable Long id,

            @RequestHeader(value = "Authorization", required = false)
            String authHeader

    ){

        try{

            // CHECK HEADER

            if(

                authHeader == null ||

                !authHeader.startsWith("Bearer ")

            ){

                return ResponseEntity
                        .badRequest()
                        .body(
                            "Authorization Header Missing"
                        );
            }

            // TOKEN

            String token =
                    authHeader.substring(7);

            // EMAIL

            String email =
                    jwtService.extractUsername(
                            token
                    );

            // CURRENT ADMIN

            User currentAdmin =
                    userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->

                        new RuntimeException(
                            "Admin Not Found"
                        )
                    );

            // TARGET USER

            User targetUser =
                    userRepository
                    .findById(id)
                    .orElseThrow(() ->

                        new RuntimeException(
                            "User Not Found"
                        )
                    );

            // ROLE CHECK

            if(

                currentAdmin.getRole()
                == Role.ROLE_ADMIN

                &&

                targetUser.getRole()
                != Role.ROLE_USER

            ){

                return ResponseEntity
                        .badRequest()
                        .body(

                            "Only Super Admin Can Block Admins"
                        );
            }

            // BLOCK

            targetUser.setBlocked(true);

            targetUser.setBlockedBy(
                    currentAdmin.getEmail()
            );

            userRepository.save(
                    targetUser
            );

            return ResponseEntity.ok(
                    "User Blocked Successfully"
            );
        }

        catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                        e.getMessage()
                    );
        }
    }
    // UNBLOCK USER

    @PutMapping("/users/{id}/unblock")

    public ResponseEntity<?> unblockUser(

            @PathVariable Long id

    ){

        User user =
                userRepository
                .findById(id)
                .orElseThrow();

        user.setBlocked(false);

        user.setBlockedBy(null);

        userRepository.save(user);

        return ResponseEntity.ok(
                "User Unblocked"
        );
    }
    // DELETE MEDICATION

    @DeleteMapping("/medications/{id}")
    public ResponseEntity<?> deleteMedication(
            @PathVariable Long id
    ){

        medicationRepository.deleteById(id);

        return ResponseEntity.ok(
                "Medication deleted successfully"
        );
    }

    // DELETE REMINDER

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<?> deleteReminder(
            @PathVariable Long id
    ){

        reminderLogRepository.deleteById(id);

        return ResponseEntity.ok(
                "Reminder deleted successfully"
        );
    }
    

    
}