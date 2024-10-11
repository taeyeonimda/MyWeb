package com.MyWeb.user.controller;

import com.MyWeb.common.FileRename;
import com.MyWeb.mail.service.MailService;
import com.MyWeb.user.dto.UserDto;
import com.MyWeb.user.entity.CustomUserDetails;
import com.MyWeb.user.entity.User;
import com.MyWeb.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@Log4j2
public class UserController {

    @Value("${file.user-profile}")
    private String uploadDir;

    private final UserService userService;
    private final FileRename fileRename;
    private final MailService mailService;

    public UserController(UserService userService, FileRename fileRename, MailService mailService) {
        this.userService = userService;
        this.fileRename = fileRename;
        this.mailService = mailService;
    }


    @PostMapping("/user/register")
    @ResponseBody
    public String insertUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "유효성 검사에 실패했습니다.";
        }
//        System.out.println("userDto :::: "+userDto);

        User user = new User();
        user.setUserEmail(userDto.getUserEmail());
        user.setUserPwd(userDto.getUserPwd());
        user.setNickName(userDto.getNickName());
        user.setAddress(userDto.getAddress());
        user.setAddress2(userDto.getAddress2());
        int userSaveResult = userService.saveUser(user);

//        System.out.println("인서트 컨트롤러 users => " + user);
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
//        System.out.println("checkEmail => "+user);
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
     * 마이페이지에서 유저 수정하기
     */
    @ResponseBody
    @PostMapping(value="/user/modifiedUser", produces = "application/json;charset=utf-8")
    public ResponseEntity<String> changeUser(
            @RequestParam("id")Long id, @RequestParam("profilePhoto")MultipartFile file,
            @RequestParam("nickName")String nickName, @RequestParam("address")String address,
            @RequestParam("address2") String address2
            )throws UnsupportedEncodingException {
        try {
            String fileName = null;
            if(!(file.isEmpty())){
                Path uploadPath = Paths.get(uploadDir);
                if(!Files.exists(uploadPath)){
                    Files.createDirectories(uploadPath);
                }
                fileName = file.getOriginalFilename();
                fileName = fileRename.fileRename(uploadPath, fileName);

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(file.getInputStream(),filePath);
            }
                User u = new User();
                u.setId(id);
                u.setProfilePhoto(fileName);
                u.setNickName(nickName);
                u.setAddress(address);
                u.setAddress2(address2);
                userService.modifierUser(u);

            return ResponseEntity.ok("하잉");
        }catch(Exception e){
            return ResponseEntity.status(500).body("파일 업로드 실패");

        }
    }

    @PostMapping("/user/getPassword")
    @ResponseBody
    public void getMyPassword(@RequestParam("userEmail")String userEmail
    ){
        log.info("UserEmail => {} ",userEmail);
        Optional<User> findUser  = userService.findByUserEmail(userEmail);

        if(findUser.isPresent()){
            log.info(" FIND USER => {}",findUser);
            String findEmail = findUser.get().getUserEmail();
            mailService.sendMail(findEmail);
        }else{
            System.out.println("없는 이메일입니다.");
        }
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
    /**
     * 마이페이지 이동페이지
     */
    @GetMapping("/myPage")
    public String myPage(){
        return "user/myPage";
    }

    /**
     * 비밀번호 찾기 이동페이지
     */
    @GetMapping("/findPassword")
    public String goFindPassword(){
        return "user/findPassword";
    }

}
