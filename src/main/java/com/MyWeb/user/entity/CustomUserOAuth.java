package com.MyWeb.user.entity;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CustomUserOAuth implements OAuth2User {

    @Getter
    private final User user;
    private  Map<String, Object> attributes;

    public CustomUserOAuth(User user,Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    public CustomUserOAuth(User user) {
        this.user = user;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return user.getUserEmail();
    }
}
