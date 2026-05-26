
package com.pillreminder.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pillreminder.config.JwtService;
import com.pillreminder.entity.EmergencyContact;
import com.pillreminder.entity.User;
import com.pillreminder.repository.EmergencyContactRepository;
import com.pillreminder.repository.UserRepository;
import com.pillreminder.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor

public class EmergencyController {

    private final EmergencyContactRepository
            emergencyContactRepository;

    private final UserRepository
            userRepository;

    private final JwtService
            jwtService;

    private final MailService
            mailService;

    // =========================
    // SAVE CONTACT
    // =========================

    @PostMapping("/contacts")

    public ResponseEntity<?> saveContact(

            @RequestBody
            EmergencyContact contact,

            @RequestHeader("Authorization")
            String authHeader

    ){

        try{

            String token =
                    authHeader.substring(7);

            String email =
                    jwtService.extractUsername(
                            token
                    );

            User user =
                    userRepository
                    .findByEmail(email)
                    .orElseThrow();

            contact.setUser(user);

            EmergencyContact saved =
                    emergencyContactRepository
                    .save(contact);

            return ResponseEntity.ok(saved);

        }

        catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Failed To Save Contact"
                    );
        }
    }

    // =========================
    // GET CONTACTS
    // =========================

    @GetMapping("/contacts")

    public ResponseEntity<?> getContacts(

            @RequestHeader("Authorization")
            String authHeader

    ){

        try{

            String token =
                    authHeader.substring(7);

            String email =
                    jwtService.extractUsername(
                            token
                    );

            User user =
                    userRepository
                    .findByEmail(email)
                    .orElseThrow();

            List<EmergencyContact>
                    contacts =

                    emergencyContactRepository
                    .findByUser(user);

            return ResponseEntity.ok(
                    contacts
            );

        }

        catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Failed To Load Contacts"
                    );
        }
    }

    // =========================
    // SEND EMERGENCY MAIL
    // =========================

    @PostMapping("/send")

    public ResponseEntity<?> sendEmergencyMail(

            @RequestHeader("Authorization")
            String authHeader

    ){

        try{

            String token =
                    authHeader.substring(7);

            String email =
                    jwtService.extractUsername(
                            token
                    );

            User user =
                    userRepository
                    .findByEmail(email)
                    .orElseThrow();

            List<EmergencyContact>
                    contacts =

                    emergencyContactRepository
                    .findByUser(user);

            if(contacts.isEmpty()){

                return ResponseEntity
                        .badRequest()
                        .body(
                            "No Emergency Contacts Found"
                        );
            }

            for(EmergencyContact c
                    : contacts){

                System.out.println(
                        c.getEmail()
                );

                String subject =

                        "🚨 Emergency Alert";

                String body =

                        "Emergency Alert!\n\n"

                        +

                        user.getFullName()

                        +

                        " may need immediate help.\n\n"

                        +

                        "Please contact immediately.";

                mailService.sendMail(

                        c.getEmail(),

                        subject,

                        body
                );
            }

            return ResponseEntity.ok(
                    "Emergency Emails Sent"
            );
        }

        catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Failed To Send Emails"
                    );
        }
    }

    // =========================
    // SAFE MESSAGE
    // =========================

    @PostMapping("/safe")

    public ResponseEntity<?> sendSafeMail(

            @RequestHeader("Authorization")
            String authHeader

    ){

        try{

            String token =
                    authHeader.substring(7);

            String email =
                    jwtService.extractUsername(
                            token
                    );

            User user =
                    userRepository
                    .findByEmail(email)
                    .orElseThrow();

            List<EmergencyContact>
                    contacts =

                    emergencyContactRepository
                    .findByUser(user);

            for(EmergencyContact c
                    : contacts){

                String subject =

                        "✅ No Emergency";

                String body =

                        user.getFullName()

                        +

                        " is safe.\n\n"

                        +

                        "Sorry for disturbance.";

                mailService.sendMail(

                        c.getEmail(),

                        subject,

                        body
                );
            }

            return ResponseEntity.ok(
                    "Safe Emails Sent"
            );
        }

        catch(Exception e){

            e.printStackTrace();

            return ResponseEntity
                    .badRequest()
                    .body(
                        "Failed To Send Safe Emails"
                    );
        }
    }
}