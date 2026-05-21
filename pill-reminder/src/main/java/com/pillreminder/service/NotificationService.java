package com.pillreminder.service;

import com.pillreminder.entity.*;
import java.time.LocalDateTime;

public interface NotificationService {
    void sendReminderNotification(User user, Medication medication, LocalDateTime scheduledTime);
    void sendRefillReminder(User user, Medication medication);
    void scheduleSnoozeNotification(User user, Medication medication, LocalDateTime newTime);
    void sendWelcomeEmail(User user);}
