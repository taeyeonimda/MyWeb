package com.MyWeb.user.service;

import com.MyWeb.user.entity.CustomUserOAuth;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String providerId = "";

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String,Object> attributes =  oAuth2User.getAttributes();

        String email = null;
        String google = "google";
        String naver = "naver";
        String kakao = "kakao";

        if(google.equals(registrationId)){
            email = String.valueOf(attributes.get("email"));
            providerId = String.valueOf(attributes.get("sub"));
        }else if(naver.equals(registrationId)){
            Map<String,Object> naverAccount = (Map<String, Object>) attributes.get("response");
            email = String.valueOf(naverAccount.get("email"));
            providerId = String.valueOf(naverAccount.get("id"));
        }else if(kakao.equals(registrationId)){
            Map<String,Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = String.valueOf(kakaoAccount.get("email"));
            providerId = String.valueOf(attributes.get("id"));
        }

        if(email == null){
            throw new OAuth2AuthenticationException("Email not found From OAuth2User");
        }

        Optional<User> findUser = userRepository.findByUserEmail(email);
        User user;

        if (findUser.isEmpty()) {
            user = new User();
            user.setUserEmail(email);
            user.setProvider(registrationId);
            user.setProviderId(providerId);
            user.setNickName(email);
            userRepository.save(user);
        } else {
            user = findUser.get();
        }

        return new CustomUserOAuth(user, attributes);
    }
}
