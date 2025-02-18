package com.example.RentalCar.controller;

import com.example.RentalCar.model.User;
import com.example.RentalCar.model.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @GetMapping(value = "/")
    public String getPage(){
        return "Welcome";
    }

    @Autowired
    private UserRepo userRepo;


    @PutMapping(value = "/update/{id}")
    public String UpdateUser(@RequestBody User user ,@PathVariable Long id){
        Optional<User> optionalUser=userRepo.findById(id);
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
