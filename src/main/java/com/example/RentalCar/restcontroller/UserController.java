package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.DTO.ApiResponse;
import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.UserRepo;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Validated
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment env;

    @Autowired
    private UserRepo userRepo;

    // Custom response DTO for consistent API responses


    @GetMapping(value = "/")
    public ResponseEntity<ApiResponse> getPage() {
        try {
            String debugMessage = String.format(
                    "Instance %s working well on PORT:%s | log level debug",
                    env.getProperty("spring.application.name"),
                    env.getProperty("server.port")
            );
            log.info(debugMessage);
            return ResponseEntity.ok(new ApiResponse(debugMessage, null, null));
        } catch (Exception e) {
            log.error("Failed to retrieve service information: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve service information",
                    e
            );
        }
    }

    @PostMapping("/admin/user/create")
    public ResponseEntity<ApiResponse<User>> createUser( @RequestBody User user) {
        try {
            // Additional validation for null checks
            if (user.getEmail() == null || user.getPassword() == null) {
                log.warn("Invalid user data: email or password is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(null, null, "Email and password are required"));
            }

            // Validate dateOfBirth
            if (user.getDateOfBirth() != null) {
                // Ensure date is in the past
                if (user.getDateOfBirth().isAfter(LocalDate.now())) {
                    log.warn("Invalid date of birth: {} is in the future", user.getDateOfBirth());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ApiResponse<>(null, null, "Date of birth must be in the past"));
                }
            } else {
                log.warn("Invalid user data: dateOfBirth is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(null, null, "Date of birth is required"));
            }

            // Check for duplicate email
            if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                log.warn("Duplicate email detected: {}", user.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(null, null, "Email " + user.getEmail() + " is already registered"));
            }

            // Check for duplicate phone number
            if (user.getPhoneNumber() != null && userRepo.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
                log.warn("Duplicate phone number detected: {}", user.getPhoneNumber());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new ApiResponse<>(null, null, "Phone number " + user.getPhoneNumber() + " is already registered"));
            }

            User savedUser = userRepo.save(user);
            log.info("User created successfully with ID: {}", savedUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>("User created successfully with ID: " + savedUser.getId(), savedUser, null));
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation while creating user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(null, null, "A user with the provided email or phone number already exists"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error creating user",
                    e
            );
        }
    }

    @GetMapping("/admin/user/all")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users = userRepo.findAll();
            String message = users.isEmpty() ? "No users found in the database" : "Retrieved " + users.size() + " users";
            log.info(message);
            return ResponseEntity.ok(new ApiResponse(message, users, null));
        } catch (Exception e) {
            log.error("Failed to retrieve all users: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve users",
                    e
            );
        }
    }

    @GetMapping("/auth/user/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable @NotNull Long id) {
        try {
            Optional<User> optionalUser = userRepo.findById(id);
            if (!optionalUser.isPresent()) {
                log.warn("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User with ID " + id + " not found", null, "Not found"));
            }

            log.info("Retrieved user with ID: {}", id);
            return ResponseEntity.ok(new ApiResponse("User retrieved successfully", optionalUser.get(), null));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user retrieval: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to retrieve user with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to retrieve user",
                    e
            );
        }
    }

    @PutMapping("/auth/user/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody User user, @PathVariable @NotNull Long id) {
        try {
            Optional<User> optionalUser = userRepo.findById(id);
            if (!optionalUser.isPresent()) {
                log.warn("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User with ID " + id + " not found", null, "Not found"));
            }

            User updatedUser = optionalUser.get();

            // Check for duplicate email (if changed)
            if (user.getEmail() != null && !user.getEmail().equals(updatedUser.getEmail())) {
                if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                    log.warn("Duplicate email detected during update: {}", user.getEmail());
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse(null, null, "Email " + user.getEmail() + " is already registered"));
                }
                updatedUser.setEmail(user.getEmail());
            }

            // Check for duplicate phone number (if changed)
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals(updatedUser.getPhoneNumber())) {
                if (userRepo.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
                    log.warn("Duplicate phone number detected during update: {}", user.getPhoneNumber());
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body(new ApiResponse(null, null, "Phone number " + user.getPhoneNumber() + " is already registered"));
                }
                updatedUser.setPhoneNumber(user.getPhoneNumber());
            }

            // Update other fields if provided
            if (user.getFirstName() != null) {
                updatedUser.setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null) {
                updatedUser.setLastName(user.getLastName());
            }
            if (user.getTitle() != null) {
                updatedUser.setTitle(user.getTitle());
            }
            if (user.getDateOfBirth() != null) {
                updatedUser.setDateOfBirth(user.getDateOfBirth());
            }
            if (user.getPassword() != null) {
                updatedUser.setPassword(user.getPassword());
            }

            userRepo.save(updatedUser);
            log.info("User with ID {} updated successfully", id);
            return ResponseEntity.ok(new ApiResponse("User updated successfully", null, null));
        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation while updating user ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(null, null, "A user with the provided email or phone number already exists"));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user update ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating user ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error updating user",
                    e
            );
        }
    }

    @DeleteMapping("/admin/user/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable @NotNull Long id) {
        try {
            Optional<User> optionalUser = userRepo.findById(id);
            if (!optionalUser.isPresent()) {
                log.warn("User not found with ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse("User with ID " + id + " not found", null, "Not found"));
            }

            userRepo.delete(optionalUser.get());
            log.info("User with ID {} deleted successfully", id);
            return ResponseEntity.ok(new ApiResponse("User deleted with ID: " + id + " successfully", null, null));
        } catch (IllegalArgumentException e) {
            log.error("Invalid input for user deletion: ID {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null, null, "Invalid input: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to delete user with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete user",
                    e
            );
        }
    }
}