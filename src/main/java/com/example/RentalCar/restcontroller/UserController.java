package com.example.RentalCar.restcontroller;

import com.example.RentalCar.model.entities.User;
import com.example.RentalCar.model.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private Environment env;

    @GetMapping(value = "/")
    public ResponseEntity<String> getPage() {
        String debugMessage = String.format(
                "Instance %s working well on PORT:%s | log level debug ",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port")

        );
        log.info(debugMessage);

        return ResponseEntity.ok(String.format("Instance %s working well on PORT:%s | log level debug ", env.getProperty("spring.application.name"), env.getProperty("server.port")));

    }

    @Autowired
    private UserRepo userRepo;


    @PutMapping(value = "/update/{id}")
    public String UpdateUser(@RequestBody User user, @PathVariable Long id) {
        Optional<User> optionalUser = userRepo.findById(id);
        if (optionalUser.isPresent()) {
            User updatedUser = optionalUser.get();
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setTitle(user.getTitle());
            updatedUser.setPhoneNumber(user.getPhoneNumber());
            updatedUser.setDateOfBirth(user.getDateOfBirth());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPassword(user.getPassword());
            userRepo.save(updatedUser);
            return "User updated successfully!";
        } else {
            return "User item with ID " + id + " not found.";
        }
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        Optional<User> optionalFood = userRepo.findById(id);

        if (optionalFood.isPresent()) {
            userRepo.delete(optionalFood.get());
            return "user deleted with ID: " + id + " successfully!";
        } else {
            return "user item with ID " + id + " not found.";
        }
    }


}
