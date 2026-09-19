package com.freeworldthing.service;

import com.freeworldthing.model.Notification;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;

    public void notify(User user, String type, String text, String link) {
        notificationRepo.save(Notification.builder()
                .user(user).type(type).text(text).link(link).build());
    }
}
