package com.example.RentalCar.service;

import com.example.RentalCar.exception.EmailAlreadyExistsException;
import com.example.RentalCar.exception.PhoneNumberAlreadyExistsException;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.entities.Admin;
import com.example.RentalCar.model.repo.UserRepo;
import com.example.RentalCar.model.repo.AdminRepo;
import com.example.RentalCar.restcontroller.response.AuthenticationResponse;
import com.example.RentalCar.service.implementation.UserDetailsImp;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepo userRepo;
    private final AdminRepo adminRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsImp userDetailsService;

    public AuthenticationService(UserRepo userRepo, AdminRepo adminRepo,
                                 PasswordEncoder passwordEncoder, JwtService jwtService,
                                 AuthenticationManager authenticationManager, UserDetailsImp userDetailsService) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public AuthenticationResponse register(User request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("A user with this email already exists.");
        }

        if (userRepo.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new PhoneNumberAlreadyExistsException("A user with this phone number already exists.");
        }

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

    public AuthenticationResponse registerAdmin(Admin request) {
        if (adminRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("An admin with this email already exists.");
        }

        Admin admin = new Admin();
        admin.setFullname(request.getFullname());
        admin.setEmail(request.getEmail());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin = adminRepo.save(admin);

        String token = jwtService.generateToken(admin);
        return new AuthenticationResponse(token);
    }

    public AuthenticationResponse authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateToken(userDetails);
        return new AuthenticationResponse(token);
    }
}
