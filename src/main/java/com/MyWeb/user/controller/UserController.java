package com.MyWeb.user.controller;

import com.MyWeb.user.dto.UserDto;
import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping("/user/register")
//    @ResponseBody
//    public String insertUser(User u,@RequestParam("userEmail") String userEmail,
//                             @RequestParam("userPwd") String userPwd,
//                             @RequestParam("nickName") String nickName,
//                             @RequestParam("address") String address,
//                             @RequestParam("address2") String address2) {
//        User users = new User();
//        users.setUserEmail(userEmail);
//        users.setUserPwd(userPwd);
//        users.setNickName(nickName);
//        users.setAddress(address);
//        System.out.println("인서트 컨트롤러 users => "+users);
//
//        int user = userService.saveUser(u);
//        if (user >= 1) {
//            return "success";
//        } else {
//            return "0";
//        }
//    }

    @PostMapping("/user/register")
    @ResponseBody
    public String insertUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "유효성 검사에 실패했습니다.";
        }
        System.out.println("userDto :::: "+userDto);

        User user = new User();
        user.setUserEmail(userDto.getUserEmail());
        user.setUserPwd(userDto.getUserPwd());
        user.setNickName(userDto.getNickName());
        user.setAddress(userDto.getAddress()+userDto.getAddress2());
        int userSaveResult = userService.saveUser(user);

        System.out.println("인서트 컨트롤러 users => " + user);
        if (userSaveResult >= 1) {
            return "success";
        } else {
            return "회원가입에 실패했습니다.";
        }
    }

    /**
     *  이메일 중복 체크
     */
    @PostMapping("/user/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("userEmails") String userEmail) {
        Optional<User> user = userService.checkEmail(userEmail);
        System.out.println("checkEmail => "+user);
        return (user.isEmpty()) ? "0" : "1";
    }

    /**
     *  닉네임 중복 체크
     */
    @PostMapping("/user/checkNickName")
    @ResponseBody
    public String checkNickName(@RequestParam("nickName") String nickName){
        Optional<Object> isDuplicate = userService.isNickNameDuplicate(nickName);
        return (isDuplicate.isEmpty()) ? "NotDuplicate" : "duplicate";
    }

    /**
     * 로그인 이동 페이지
     */
    @GetMapping("/loginPage")
    public String loginForm(){
        return "user/loginForm";
    }

    /**
     * 회원가입 이동페이지
     */
    @GetMapping ("/newUser")
    public String newUser(){
        return "user/register";
    }

}
