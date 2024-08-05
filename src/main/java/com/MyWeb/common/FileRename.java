package com.MyWeb.common;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class FileRename {

    public String fileRename(Path uploadPath, String fileName){
        String newFileName = fileName;
        int count = 1;

        while(Files.exists(uploadPath.resolve(newFileName))){
            String fileBaseName = getFileName(fileName);
            String fileExtension = getFileExtension(fileName);
            newFileName = fileBaseName + "-" + count + fileExtension;
            count++;
        }
        return newFileName;
    }

    private String getFileName(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex==-1) ? fileName : fileName.substring(0,dotIndex);
    }

    private String getFileExtension(String fileName){
        int dotIndex = fileName.lastIndexOf(".");
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex);
    }
}
