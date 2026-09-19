package com.freeworldthing.controller;

import com.freeworldthing.model.Message;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.MessageRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepo;
    private final UserRepository userRepo;
    private final NotificationService notifications;

    public record MessageDto(Long id, Long senderId, String senderName, Long recipientId,
                             String body, boolean read, String createdAt) {
        public static MessageDto from(Message m) {
            return new MessageDto(m.getId(), m.getSender().getId(), m.getSender().getFullName(),
                    m.getRecipient().getId(), m.getBody(), m.isRead(),
                    m.getCreatedAt() == null ? null : m.getCreatedAt().toString());
        }
    }

    public record ConversationDto(String key, Long otherUserId, String otherUserName,
                                  String otherUserHeadline, String lastMessage, String lastAt,
                                  long unreadCount) {}

    public record SendReq(Long recipientId, String body) {}

    private static String keyFor(Long a, Long b) {
        long min = Math.min(a, b), max = Math.max(a, b);
        return min + ":" + max;
    }

    @GetMapping("/conversations")
    public List<ConversationDto> conversations(Principal principal) {
        User me = me(principal);
        List<Message> all = messageRepo.findThreads(me.getId());
        Map<String, List<Message>> byKey = all.stream()
                .collect(Collectors.groupingBy(Message::getConversationKey, Collectors.toList()));

        return byKey.entrySet().stream()
                .map(e -> {
                    List<Message> msgs = e.getValue();
                    msgs.sort(Comparator.comparing(Message::getCreatedAt,
                            Comparator.nullsLast(Comparator.naturalOrder())));
                    Message last = msgs.get(msgs.size() - 1);
                    User other = last.getSender().getId().equals(me.getId())
                            ? last.getRecipient() : last.getSender();
                    long unread = msgs.stream().filter(m -> !m.isRead()
                            && m.getRecipient().getId().equals(me.getId())).count();
                    return new ConversationDto(e.getKey(), other.getId(), other.getFullName(),
                            other.getHeadline(), last.getBody(),
                            last.getCreatedAt() == null ? null : last.getCreatedAt().toString(), unread);
                })
                .sorted(Comparator.comparing(ConversationDto::lastAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @GetMapping("/threads/{otherUserId}")
    public List<MessageDto> thread(Principal principal, @PathVariable Long otherUserId) {
        User me = me(principal);
        String key = keyFor(me.getId(), otherUserId);
        List<Message> msgs = messageRepo.findByConversationKeyOrderByCreatedAtAsc(key);
        // mark inbound messages as read
        boolean changed = false;
        for (Message m : msgs) {
            if (!m.isRead() && m.getRecipient().getId().equals(me.getId())) {
                m.setRead(true);
                changed = true;
            }
        }
        if (changed) messageRepo.saveAll(msgs);
        return msgs.stream().map(MessageDto::from).toList();
    }

    @PostMapping("/send")
    public MessageDto send(Principal principal, @RequestBody SendReq req) {
        User me = me(principal);
        User recipient = userRepo.findById(req.recipientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipient not found"));
        if (recipient.getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot message yourself");
        }
        Message m = messageRepo.save(Message.builder()
                .conversationKey(keyFor(me.getId(), recipient.getId()))
                .sender(me).recipient(recipient)
                .body(req.body())
                .build());
        notifications.notify(recipient, "MESSAGE",
                "New message from " + me.getFullName(), "/messages/" + me.getId());
        return MessageDto.from(m);
    }

    private User me(Principal principal) {
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }
}
