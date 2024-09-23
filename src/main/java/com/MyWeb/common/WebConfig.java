package com.MyWeb.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
//    @Value("${file.user-profile}")
//    private String profileImg;
//
//    @Value("${file.chatImg}")
//    private String chatImage;
//
//    @Value("${file.uploads}")
//    private String boardImage;

    @Value("${file.uploads}")
    private String uploadDir;

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/uploads/**")
//                .addResourceLocations("file:" + profileImg, "file:" + chatImage, "file:" +boardImage);
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);

    }
}
