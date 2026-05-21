package com.pillreminder.service;

public interface MailService {

    // Medication Reminder Mail
    void sendMail(
            String to,
            String medicationName,
            String dosage,
            String reminderTime
    );

    // Generic Mail
    void sendMail(
            String to,
            String subject,
            String body
    );
}