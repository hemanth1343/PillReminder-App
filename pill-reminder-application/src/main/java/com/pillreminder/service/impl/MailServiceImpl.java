package com.pillreminder.service.impl;

import com.pillreminder.service.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    // ==============================
    // Medication Reminder Mail
    // ==============================

    @Override
    public void sendMail(
            String to,
            String medicationName,
            String dosage,
            String reminderTime
    ) {

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            // Sender Mail
            message.setFrom(
                    "mkakarla610@gmail.com"
            );

            // Receiver Mail
            message.setTo(to);

            // Subject
            message.setSubject(
                    "💊 Prescription Connect - Medication Reminder"
            );

            // Mail Body
            String body =

                    "Hello,\n\n" +

                            "⏰ It's time to take your medication.\n\n" +

                            "💊 Medicine Name : " + medicationName + "\n" +

                            "💉 Dosage : " + dosage + "\n" +

                            "🕒 Reminder Time : " + reminderTime + "\n\n" +

                            "Please take your medicine on time.\n" +

                            "Stay healthy and take care ❤️\n\n" +

                            "Regards,\n" +
                            "Prescription Connect";

            // Set Mail Content
            message.setText(body);

            // Send Mail
            mailSender.send(message);

            System.out.println(
                    "✅ Reminder Mail Sent Successfully"
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Failed To Send Reminder Mail"
            );

            e.printStackTrace();
        }
    }

    // ==============================
    // Generic Mail Method
    // ==============================

    @Override
    public void sendMail(
            String to,
            String subject,
            String body
    ) {

        try {

            SimpleMailMessage message =
                    new SimpleMailMessage();

            message.setFrom(
                    "mkakarla610@gmail.com"
            );

            message.setTo(to);

            message.setSubject(subject);

            message.setText(body);

            mailSender.send(message);

            System.out.println(
                    "✅ Generic Mail Sent Successfully"
            );

        } catch (Exception e) {

            System.out.println(
                    "❌ Failed To Send Generic Mail"
            );

            e.printStackTrace();
        }
    }
}