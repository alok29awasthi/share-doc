package com.sharedoc.app.service;

import com.sharedoc.app.dto.CustomUserDetails;
import com.sharedoc.app.entity.User;
import com.sharedoc.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // Or however you load users

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Replace with your actual user fetching logic
         User user = userRepository.findByEmail(email).orElseThrow();
         return new CustomUserDetails(user);
    }
}
