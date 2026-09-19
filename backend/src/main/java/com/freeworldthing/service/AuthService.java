package com.freeworldthing.service;

import com.freeworldthing.config.JwtService;
import com.freeworldthing.model.User;
import com.freeworldthing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public record AuthResponse(String token, UserDto user) {}

    public record UserDto(Long id, String email, String fullName, User.Role role, String headline,
                          String bio, String location, Double hourlyRate, String availability,
                          Double ratingAvg, Integer ratingCount, Integer completedProjects,
                          boolean identityVerified, boolean paymentVerified) {
        public static UserDto from(User u) {
            return new UserDto(u.getId(), u.getEmail(), u.getFullName(), u.getRole(), u.getHeadline(),
                    u.getBio(), u.getLocation(), u.getHourlyRate(),
                    u.getAvailability() == null ? null : u.getAvailability().name(),
                    u.getRatingAvg(), u.getRatingCount(), u.getCompletedProjects(),
                    u.isIdentityVerified(), u.isPaymentVerified());
        }
    }

    public AuthResponse register(String email, String password, String fullName, User.Role role) {
        String norm = email.trim().toLowerCase();
        if (userRepo.existsByEmail(norm)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        User u = User.builder()
                .email(norm)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName.trim())
                .role(role)
                .build();
        userRepo.save(u);
        return new AuthResponse(jwtService.issue(u.getId(), u.getEmail(), u.getRole()), UserDto.from(u));
    }

    public AuthResponse login(String email, String password) {
        User u = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        return new AuthResponse(jwtService.issue(u.getId(), u.getEmail(), u.getRole()), UserDto.from(u));
    }
}
