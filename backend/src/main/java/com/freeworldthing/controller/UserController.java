package com.freeworldthing.controller;

import com.freeworldthing.model.User;
import com.freeworldthing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * User directory + wallet. Never exposes password hashes: every response goes
 * through UserView. Wallet deposits are restricted to the account owner or admin.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    public record UserView(Long id, String email, String fullName, String role, String headline, String location,
                           Double hourlyRate, Double walletBalance, String availability,
                           Double ratingAvg, Integer ratingCount, Integer completedProjects,
                           boolean identityVerified, boolean paymentVerified) {
        public static UserView from(User u) {
            return new UserView(u.getId(), u.getEmail(), u.getFullName(), u.getRole().name(), u.getHeadline(),
                    u.getLocation(), u.getHourlyRate(), u.getWalletBalance(),
                    u.getAvailability() == null ? null : u.getAvailability().name(),
                    u.getRatingAvg(), u.getRatingCount(), u.getCompletedProjects(),
                    u.isIdentityVerified(), u.isPaymentVerified());
        }
    }

    /** Current authenticated user — the session anchor for the frontend. */
    @GetMapping("/me")
    public UserView me(Principal principal) {
        return UserView.from(currentUser(principal));
    }

    @GetMapping
    public List<UserView> getAllUsers() {
        return userRepo.findAll().stream().map(UserView::from).toList();
    }

    @GetMapping("/{id}")
    public UserView getUserById(@PathVariable Long id) {
        return userRepo.findById(id).map(UserView::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PostMapping("/{id}/wallet/deposit")
    public UserView depositToWallet(Principal principal, @PathVariable Long id, @RequestParam Double amount) {
        User me = currentUser(principal);
        if (!me.getId().equals(id) && me.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("You can only deposit to your own wallet");
        }
        if (amount == null || amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }
        double current = me.getWalletBalance() != null ? me.getWalletBalance() : 0.0;
        me.setWalletBalance(current + amount);
        return UserView.from(userRepo.save(me));
    }

    private User currentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
