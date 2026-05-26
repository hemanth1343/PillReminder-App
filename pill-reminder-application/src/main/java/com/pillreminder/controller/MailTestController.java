package com.pillreminder.controller;

import com.pillreminder.service.MailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTestController {

    @Autowired
    private MailService mailService;

    @GetMapping("/send-test-mail")
    public String sendTestMail() {

        mailService.sendMail(

                "YOUR_RECEIVER_EMAIL@gmail.com",

                "Paracetamol",

                "500mg",

                "08:00 AM"
        );

        return "Mail Sent Successfully";
    }
}