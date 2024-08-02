package com.MyWeb.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Getter
@NoArgsConstructor
public class UserDto {
    @NotEmpty(message = "이메일은 필수 항목입니다.")
    private String userEmail;

    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String userPwd;

    @NotEmpty(message = "닉네임은 필수 항목입니다.")
    private String nickName;

    @NotEmpty(message = "주소는 필수 항목입니다.")
    private String address;

    @NotEmpty(message ="주소2도 필수 항목입니다.")
    private String address2;
}
