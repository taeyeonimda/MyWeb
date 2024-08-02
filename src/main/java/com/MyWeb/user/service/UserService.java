package com.MyWeb.user.service;

import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("들어오는거 확인 ::" + username);
        Optional<User> userOptional  = userRepository.findByUserEmail(username);
        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException("User not found : " + username);
        }
        User user = userOptional.get();

        System.out.println("user :" + user);

        return new CustomUserDetails(user);
    }
}
