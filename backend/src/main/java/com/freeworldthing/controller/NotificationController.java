package com.freeworldthing.controller;

import com.freeworldthing.model.Notification;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.NotificationRepository;
import com.freeworldthing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepo;
    private final UserRepository userRepo;

    public record NotificationDto(Long id, String type, String text, String link, boolean read, String createdAt) {
        public static NotificationDto from(Notification n) {
            return new NotificationDto(n.getId(), n.getType(), n.getText(), n.getLink(), n.isRead(),
                    n.getCreatedAt() == null ? null : n.getCreatedAt().toString());
        }
    }

    @GetMapping
    public List<NotificationDto> list(Principal principal) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(me(principal).getId()).stream()
                .map(NotificationDto::from)
                .toList();
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Principal principal) {
        return Map.of("count", notificationRepo.countByUserIdAndReadFalse(me(principal).getId()));
    }

    @PostMapping("/read-all")
    public Map<String, String> markAllRead(Principal principal) {
        List<Notification> unread = notificationRepo.findByUserIdOrderByCreatedAtDesc(me(principal).getId()).stream()
                .filter(n -> !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(unread);
        return Map.of("status", "OK", "updated", String.valueOf(unread.size()));
    }

    @PostMapping("/{id}/read")
    public Map<String, String> markRead(Principal principal, @PathVariable Long id) {
        Notification n = notificationRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!n.getUser().getId().equals(me(principal).getId())) {
            throw new AccessDeniedException("Not your notification");
        }
        n.setRead(true);
        notificationRepo.save(n);
        return Map.of("status", "OK");
    }

    private User me(Principal principal) {
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
