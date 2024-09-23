package com.MyWeb.File.dto;

import com.MyWeb.File.entity.File;
import com.MyWeb.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDTO {
    private Long id;
    private String fileName;
    private String filePath;
    private String type;
    private Long refId;
    private User user;
    private Long userId;

    public File toEntity() {
        return File.builder()
                .id(this.id)
                .fileName(this.fileName)
                .filePath(this.filePath)
                .type(this.type)
                .refId(this.refId)
                .user(this.user) // userId로 User 객체를 생성
                .build();
    }
}
