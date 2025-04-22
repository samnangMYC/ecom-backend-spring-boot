package com.samnang.ecommerce.util;

import com.samnang.ecommerce.models.User;
import com.samnang.ecommerce.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;


    public String loggedInEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user.getEmail();
    }
    public Long loggedInUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user.getUserId();
    }

    public User loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUserName(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
    }
}
