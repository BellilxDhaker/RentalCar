package com.example.RentalCar.service.implementation;

import com.example.RentalCar.model.repo.UserRepo;
import com.example.RentalCar.model.repo.AdminRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsImp implements UserDetailsService {

    private final UserRepo userRepo;
    private final AdminRepo adminRepo;

    public UserDetailsImp(UserRepo userRepo, AdminRepo adminRepo) {
        this.userRepo = userRepo;
        this.adminRepo = adminRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return adminRepo.findByEmail(email)
                .map(admin -> (UserDetails) admin)
                .orElseGet(() -> userRepo.findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("User or Admin not found with email: " + email)));
    }
}