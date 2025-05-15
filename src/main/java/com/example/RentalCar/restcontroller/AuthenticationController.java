package com.example.RentalCar.restcontroller;

import com.example.RentalCar.exception.EmailAlreadyExistsException;
import com.example.RentalCar.exception.PhoneNumberAlreadyExistsException;
import com.example.RentalCar.model.repo.UserRepo;
import com.example.RentalCar.restcontroller.response.ApiResponse;
import com.example.RentalCar.model.entities.Admin;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.restcontroller.request.LoginRequest;
import com.example.RentalCar.restcontroller.response.AuthenticationResponse;
import com.example.RentalCar.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    private  UserRepo userRepo;
    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<Map<String, String>>> registerUser(
            @RequestBody User user,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(null, null, "Invalid user input: please check all required fields.")
                );
            }

            AuthenticationResponse response = authenticationService.register(user);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", response.getToken());
            responseData.put("email", user.getEmail());
            responseData.put("name", user.getFirstName() +" "+ user.getLastName());

            return ResponseEntity.ok(new ApiResponse<>("User registration completed successfully.", responseData, null));

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
    public ResponseEntity<ApiResponse<Map<String, String>>> registerAdmin(
            @RequestBody Admin admin,
            BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(null, null, "Invalid admin input: please check all required fields.")
                );
            }

            AuthenticationResponse response = authenticationService.registerAdmin(admin);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", response.getToken());
            responseData.put("email", admin.getEmail());

            return ResponseEntity.ok(new ApiResponse<>("Admin registration completed successfully.", responseData, null));

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
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginRequest request) {
        try {
            AuthenticationResponse authResponse = authenticationService.authenticate(
                    request.getEmail(), request.getPassword()
            );

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", authResponse.getToken());
            responseData.put("email", request.getEmail());
            responseData.put("name", userRepo.findByEmail(request.getEmail()).get().getFirstName() +" "+ userRepo.findByEmail(request.getEmail()).get().getLastName());

            return ResponseEntity.ok(new ApiResponse<>("Login successful.", responseData, null));
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