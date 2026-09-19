package com.freeworldthing.controller;

import com.freeworldthing.model.User;
import com.freeworldthing.repository.UserRepository;
import com.freeworldthing.service.AuthService;
import com.freeworldthing.service.AuthService.UserDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;

    public record RegisterReq(@Email @NotBlank String email,
                              @NotBlank String password,
                              @NotBlank String fullName,
                              String role) {}

    public record LoginReq(@Email @NotBlank String email, @NotBlank String password) {}

    public record ProfileUpdateReq(String headline, String bio, String location,
                                   Double hourlyRate, String availability) {}

    @PostMapping("/register")
    public AuthService.AuthResponse register(@Valid @RequestBody RegisterReq req) {
        User.Role role;
        try {
            role = User.Role.valueOf(req.role() == null ? "FREELANCER" : req.role().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must be CLIENT, FREELANCER or ADMIN");
        }
        return authService.register(req.email(), req.password(), req.fullName(), role);
    }

    @PostMapping("/login")
    public AuthService.AuthResponse login(@Valid @RequestBody LoginReq req) {
        return authService.login(req.email(), req.password());
    }

    @GetMapping("/me")
    public UserDto me(Principal principal) {
        return UserDto.from(currentUser(principal));
    }

    @PutMapping("/me")
    public UserDto updateProfile(Principal principal, @Valid @RequestBody ProfileUpdateReq req) {
        User u = currentUser(principal);
        if (req.headline() != null) u.setHeadline(req.headline());
        if (req.bio() != null) u.setBio(req.bio());
        if (req.location() != null) u.setLocation(req.location());
        if (req.hourlyRate() != null) u.setHourlyRate(req.hourlyRate());
        if (req.availability() != null) {
            try {
                u.setAvailability(User.Availability.valueOf(req.availability().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "availability must be AVAILABLE, WITHIN_WEEK or BUSY");
            }
        }
        userRepo.save(u);
        return UserDto.from(u);
    }

    private User currentUser(Principal principal) {
        return userRepo.findByEmail(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
