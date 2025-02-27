package com.example.RentalCar.service;

import com.example.RentalCar.model.AuthenticationResponse;
import com.example.RentalCar.model.User;
import com.example.RentalCar.model.repo.UserRepo;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User request) {
        /*if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty. User Details: " +
                    "First Name: " + request.getFirstName() + ", " +
                    "Last Name: " + request.getLastName() + ", " +
                    "Email: " + request.getEmail() + ", " +
                    "Date of Birth: " + request.getDateOfBirth() + ", " +
                    "Phone Number: " + request.getPhoneNumber() + ", " +
                    "Password: " + request.getPassword()+","+
            "Title: " + request.getTitle());

        }*/

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTitle(request.getTitle());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user = userRepo.save(user);
        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword() // Fixed issue
                )
        );
        User user = userRepo.findByEmail(request.getEmail()) // Fixed method name
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token);
    }
}
