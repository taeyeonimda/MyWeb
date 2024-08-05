package com.MyWeb.user.service;

import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.CustomUserOAuth;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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

    public Optional<User> checkEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    public Optional<Object> isNickNameDuplicate(String nickName) {
        return userRepository.findByNickName(nickName);
    }

    @Transactional
    public int saveUser(User user) {
        String encodePwd = passwordEncoder.encode(user.getUserPwd());
        user.setUserPwd(encodePwd);

        User savedUser = userRepository.save(user);

        return savedUser.getId() != null ? 1 : 0;
    }

    @Transactional
    public void modifierUser(User u) {
        Optional<User> optionalUser = userRepository.findById(u.getId());
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setProfilePhoto(u.getProfilePhoto());
            user.setNickName(u.getNickName());
            user.setAddress(u.getAddress());
            user.setAddress2(u.getAddress2());
            user.setNickName(u.getNickName());

            User savedUser = userRepository.save(user);
            updateSecurityContext(savedUser);


            System.out.println("서비스단 savedUser => "+savedUser);
        }
    }

    private void updateSecurityContext(User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetails) {
                CustomUserDetails customUserDetails = new CustomUserDetails(updatedUser);
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        customUserDetails,
                        authentication.getCredentials(),
                        authentication.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            } else if (principal instanceof CustomUserOAuth) {
                CustomUserOAuth customUserOAuth = new CustomUserOAuth(updatedUser);
                UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                        customUserOAuth,
                        authentication.getCredentials(),
                        authentication.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }
    }
}
