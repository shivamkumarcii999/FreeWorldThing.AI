package com.freeworldthing.controller;

import com.freeworldthing.model.Contract;
import com.freeworldthing.model.Review;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.ContractRepository;
import com.freeworldthing.repository.ReviewRepository;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepo;
    private final ContractRepository contractRepo;
    private final UserRepository userRepo;
    private final NotificationService notifications;

    public record LeaveReq(int overall, Integer quality, Integer communication, Integer reliability,
                           Integer deadlineAdherence, Integer requirementClarity, Integer paymentBehavior,
                           Integer scopeStability, String comment) {}

    public record ReviewDto(Long id, Long contractId, String contractTitle, String direction,
                            String authorName, Long subjectId, int overall, String comment, String createdAt) {}

    @PostMapping("/api/contracts/{contractId}/review")
    public ReviewDto leave(Principal principal, @PathVariable Long contractId, @RequestBody LeaveReq req) {
        User me = userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Contract c = contractRepo.findById(contractId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contract not found"));
        if (!c.getClient().getId().equals(me.getId()) && !c.getFreelancer().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your contract");
        }
        if (reviewRepo.existsByContractIdAndAuthorId(contractId, me.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You already reviewed this contract");
        }

        boolean amClient = c.getClient().getId().equals(me.getId());
        User subject = amClient ? c.getFreelancer() : c.getClient();

        Review r = Review.builder()
                .contract(c).author(me).subject(subject)
                .direction(amClient ? Review.Direction.CLIENT_TO_FREELANCER : Review.Direction.FREELANCER_TO_CLIENT)
                .overall(Math.max(1, Math.min(5, req.overall())))
                .quality(req.quality()).communication(req.communication()).reliability(req.reliability())
                .deadlineAdherence(req.deadlineAdherence())
                .requirementClarity(req.requirementClarity()).paymentBehavior(req.paymentBehavior())
                .scopeStability(req.scopeStability())
                .comment(req.comment())
                .build();
        r = reviewRepo.save(r);

        // recompute subject rating aggregate
        List<Review> subjectReviews = reviewRepo.findBySubjectIdOrderByCreatedAtDesc(subject.getId());
        double avg = subjectReviews.stream().mapToInt(Review::getOverall).average().orElse(req.overall());
        subject.setRatingAvg(Math.round(avg * 10.0) / 10.0);
        subject.setRatingCount(subjectReviews.size());
        userRepo.save(subject);

        notifications.notify(subject, "REVIEW_RECEIVED",
                me.getFullName() + " left a " + req.overall() + "★ review", "/talent/" + me.getId());
        return toDto(r);
    }

    @GetMapping("/api/talent/{id}/reviews")
    public List<ReviewDto> forUser(@PathVariable Long id) {
        return reviewRepo.findBySubjectIdOrderByCreatedAtDesc(id).stream()
                .map(ReviewController::toDto)
                .toList();
    }

    private static ReviewDto toDto(Review r) {
        return new ReviewDto(r.getId(), r.getContract().getId(), r.getContract().getTitle(),
                r.getDirection().name(), r.getAuthor().getFullName(), r.getSubject().getId(),
                r.getOverall(), r.getComment(),
                r.getCreatedAt() == null ? null : r.getCreatedAt().toString());
    }
}
