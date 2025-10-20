package com.example.userserver.service;

import org.springframework.stereotype.Service;

@Service
public class SmsService {
    public void sendSms(String phone, String message) {
        // integrate with provider (Twilio, etc.)
        System.out.println("SMS to " + phone + ": " + message);
    }
}
