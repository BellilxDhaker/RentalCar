package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.entities.Admin;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.restcontroller.request.LoginRequest;
import com.example.RentalCar.restcontroller.response.AuthenticationResponse;
import com.example.RentalCar.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register/user")
    public ResponseEntity<?> registerUser(@RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input: " + bindingResult.getAllErrors());
        }
        AuthenticationResponse response = authenticationService.register(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid input: " + bindingResult.getAllErrors());
        }
        AuthenticationResponse response = authenticationService.registerAdmin(admin);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AuthenticationResponse response = authenticationService.authenticate(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}

