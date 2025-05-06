package com.example.RentalCar.restcontroller;

import com.example.RentalCar.exception.EmailAlreadyExistsException;
import com.example.RentalCar.exception.PhoneNumberAlreadyExistsException;
import com.example.RentalCar.model.DTO.ApiResponse;
import com.example.RentalCar.model.entities.Admin;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.restcontroller.request.LoginRequest;
import com.example.RentalCar.restcontroller.response.AuthenticationResponse;
import com.example.RentalCar.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> registerUser(
            @RequestBody User user,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(null, null, "Invalid user input: please check all required fields.")
                );
            }

            AuthenticationResponse response = authenticationService.register(user);
            return ResponseEntity.ok(new ApiResponse<>("User registration completed successfully.", response, null));

        } catch (EmailAlreadyExistsException | PhoneNumberAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(null, null, "Registration failed: " + e.getMessage())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(null, null, "User registration failed: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(null, null, "Unexpected error occurred while registering the user.")
            );
        }
    }


    @PostMapping("/register/admin")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> registerAdmin(
            @RequestBody Admin admin,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(null, null, "Invalid admin input: please check all required fields.")
                );
            }

            AuthenticationResponse response = authenticationService.registerAdmin(admin);
            return ResponseEntity.ok(new ApiResponse<>("Admin registration completed successfully.", response, null));

        } catch (EmailAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(null, null, "Registration failed: " + e.getMessage())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(null, null, "Admin registration failed: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(null, null, "Unexpected error occurred while registering the admin.")
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(new ApiResponse<>("Login successful.", response, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(null, null, "Login failed: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(null, null, "Unexpected error occurred during login.")
            );
        }
    }
}
