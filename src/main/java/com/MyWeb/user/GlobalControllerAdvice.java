package com.MyWeb.user;

import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.CustomUserOAuth;
import com.MyWeb.user.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("currentUser")
    public User getCurrentUser(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                               @AuthenticationPrincipal OAuth2User oAuth2User) {
        if (customUserDetails != null){
            return customUserDetails.getUser();
        }else if(oAuth2User instanceof CustomUserOAuth){
            return ((CustomUserOAuth) oAuth2User).getUser();
        }else{
            return null;
        }
    }
}
