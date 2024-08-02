package com.MyWeb.common;

import com.MyWeb.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 정적 리소스는 시큐리티 상관없이 접근 가능
     */
    @Bean
    public WebSecurityCustomizer configureWebSecurityCustomizer(){
        return ( web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

    /**
     * WHITE LIST 등록 시큐리티 상관없이 접근 가능한 URL 등록
     */
    private static final String[] WHITE_LIST= {
            "/", "/loginPage", "/newUser", "/user/checkEmail", "/user/checkNickName", "/user/register"
    };



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf( csrf -> csrf.disable())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(WHITE_LIST).permitAll()
                                .anyRequest().authenticated()
                )
                .formLogin( formLogin -> formLogin
                        .loginPage("/loginPage")
                        .loginProcessingUrl("/user/getOneUser")
                        .usernameParameter("userEmail")
                        .passwordParameter("userPwd")
                        .defaultSuccessUrl("/", true)
                ).oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/loginPage")
                        .defaultSuccessUrl("/",true)
                ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );
        return http.build();
    }




    @Bean
    public PasswordEncoder PasswordEncoder () {
        return new BCryptPasswordEncoder();
    }
}
