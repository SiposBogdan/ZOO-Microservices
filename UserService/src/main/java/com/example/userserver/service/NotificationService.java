package com.example.userserver.service;

import com.example.userserver.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final SmsService smsService;

    /**
     * Notify a newly created user of their credentials (email & SMS).
     */
    public void notifyNewUser(User user, String rawPassword) {
        String subject = "Welcome to ZooApp!";
        String body = String.format(
                "Hello %s,%n%n" +
                        "Your account has been created.%n" +
                        "Username: %s%n" +
                        "Password: %s%n%n" +
                        "Please change your password on first login.%n",
                user.getUsername(), user.getUsername(), rawPassword
        );

        emailService.sendUserUpdateEmail(user.getEmail(), subject, body);
        smsService.sendSms(
                user.getPhone(),
                String.format("Hi %s, your ZooApp account is ready. Check email for details.", user.getUsername())
        );
    }

    /**
     * Notify an existing user that their account was updated.
     * If the passwordChanged is true, include the new password.
     */
    public void notifyOnUpdate(User user, boolean passwordChanged, String rawPassword) {
        String subject = "Your ZooApp Account Was Updated";
        StringBuilder body = new StringBuilder()
                .append("Hello ").append(user.getUsername()).append(",\n\n")
                .append("Your account details have been updated.\n");

        if (passwordChanged) {
            body.append("New Password: ").append(rawPassword).append("\n");
        }

        body.append("\nIf you did not request this change, please contact support.");

        emailService.sendUserUpdateEmail(user.getEmail(), subject, body.toString());
        smsService.sendSms(
                user.getPhone(),
                "Your ZooApp account was updated. Check your email for details."
        );
    }
}
