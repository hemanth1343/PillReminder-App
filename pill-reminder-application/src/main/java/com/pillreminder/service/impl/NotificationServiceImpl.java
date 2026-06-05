package com.pillreminder.service.impl;

import com.pillreminder.entity.*;
import com.pillreminder.service.NotificationService;

import jakarta.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired(required = false)
	private JavaMailSender mailSender;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' hh:mm a");

	// =========================================
	// Medication Reminder Mail
	// =========================================

	@Override
	@Async
	public void sendReminderNotification(User user, Medication medication, LocalDateTime scheduledTime) {

		if (mailSender == null || !user.isEmailNotifications()) {

			log.debug("Mail skipped for reminder: {} - {}", user.getEmail(), medication.getName());

			return;
		}

		try {

			MimeMessage message = mailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(user.getEmail());

			helper.setSubject("💊 Prescription Connect - Medication Added");

			String htmlContent =

					"<div style='font-family:Arial,sans-serif;" + "max-width:600px;" + "margin:auto;" + "padding:20px;"
							+ "background:#f4f7fb;" + "border-radius:12px;'>"

							+

							"<div style='background:#4f46e5;" + "padding:20px;" + "border-radius:10px;" + "color:white;"
							+ "text-align:center;'>"

							+

							"<h1>💊 Prescription Connect</h1>" +

"<p>Medication Added Successfully</p>"

							+

							"</div>"

							+

							"<div style='background:white;" + "padding:25px;" + "margin-top:20px;"
							+ "border-radius:10px;'>"

							+

							"<h2>Hello " + user.getFullName() + ",</h2>"

							+

							"<p>Your medication has been successfully added to Prescription Connect.</p>"

							+

							"<table style='width:100%;" + "border-collapse:collapse;'>"

							+

							"<tr>" + "<td><b>Medicine</b></td>" + "<td>" + medication.getName() + "</td>" + "</tr>"

							+

							"<tr>" + "<td><b>Dosage</b></td>" + "<td>" + medication.getDosage() + "</td>" + "</tr>"

							+

							"<tr>" + "<td><b>Reminder Time</b></td>" + "<td>" + medication.getScheduledTimeList()
					        .stream()
					        .map(time -> time.toString())
					        .reduce((a,b) -> a + ", " + b)
					        .orElse("No Time")
							+ "</td>" + "</tr>"

							+

							"<tr>" + "<td><b>Instructions</b></td>" + "<td>" +

							(medication.getInstructions() != null && !medication.getInstructions().trim().isEmpty()

									? medication.getInstructions()

									: "Take as prescribed by your doctor")

							+

							"</td>" + "</tr>"

							+

							"</table>"

							+

							"<div style='margin-top:25px;" + "padding:15px;" + "background:#eef2ff;"
							+ "border-radius:8px;'>"

							+

							"❤️ Stay healthy and take your medicine on time."

							+

							"</div>"

							+

							"</div>"

							+

							"<div style='text-align:center;" + "margin-top:15px;" + "color:#64748b;'>"

							+

							"Prescription Connect © 2026"

							+

							"</div>"

							+

							"</div>";

			helper.setText(htmlContent, true);

			mailSender.send(message);

			log.info(" HTML reminder mail sent to {}", user.getEmail());

		} catch (Exception e) {

			log.error("Failed to send HTML reminder email: {}", e.getMessage());
		}
	}

	// =========================================
	// Refill Reminder Mail
	// =========================================

	@Override
	@Async
	public void sendRefillReminder(User user, Medication medication) {

		if (mailSender == null || !user.isEmailNotifications()) {

			return;
		}

		try {

			SimpleMailMessage message = new SimpleMailMessage();

			message.setTo(user.getEmail());

			message.setSubject(" Low Supply Alert");

			message.setText(

					"Hi " + user.getFullName() + ",\n\n" +

							"Your medicine stock is running low.\n\n" +

							"Medicine : " + medication.getName() + "\n" +

							"Remaining Pills : " + medication.getRemainingPills() +

							"\n\nPlease refill your medication soon.");

			mailSender.send(message);

		} catch (Exception e) {

			log.error("Failed to send refill email: {}", e.getMessage());
		}
	}

	// =========================================
	// Snooze Notification
	// =========================================

	@Override
	@Async
	public void scheduleSnoozeNotification(User user, Medication medication, LocalDateTime newTime) {

		log.info("Snooze scheduled for {} - {} at {}", user.getEmail(), medication.getName(), newTime);
	}

	// =========================================
	// Welcome Mail
	// =========================================

	@Override
	@Async
	public void sendWelcomeEmail(User user) {

		if (mailSender == null)
			return;

		try {

			SimpleMailMessage message = new SimpleMailMessage();

			message.setTo(user.getEmail());

			message.setSubject("🎉 Welcome to Prescription Connect");

			message.setText(

					"Hi " + user.getFullName() + ",\n\n" +

							"Welcome to Prescription Connect!\n\n" +

							"Your account has been created successfully.\n\n" +

							"Stay healthy ❤️");

			mailSender.send(message);

			log.info("✅ Welcome email sent to {}", user.getEmail());

		} catch (Exception e) {

			log.error("Failed to send welcome email: {}", e.getMessage());
		}
	}

}