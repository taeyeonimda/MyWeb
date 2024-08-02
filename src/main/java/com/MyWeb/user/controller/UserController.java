package com.MyWeb.user.controller;

import com.MyWeb.user.entity.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {


//    @GetMapping("/logout")
//    public UserDetails logout(String username)throws UsernameNotFoundException{
//        System.out.println("logout 들어오는지 확인");
//        System.out.println(username);
//        return new CustomUserDetails(null);
//    }


    @GetMapping("/loginPage")
    public String loginForm(){
        return "user/loginForm";
    }

}
