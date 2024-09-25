package com.MyWeb.File.service;

import com.MyWeb.File.entity.File;
import com.MyWeb.File.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.expression.Lists;

import java.util.*;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void FileSaves(){

    }

    @Transactional(rollbackFor = Exception.class)
    public File save(File entityFile) {
        return fileRepository.save(entityFile);
    }

    public List<File> getAllFile(Long id) {
        return fileRepository.findAllByRefId(id);
    }


    @Transactional(rollbackFor = Exception.class)
    public void delFiles(String delValues) {
        System.out.println("서비스 DelValues => "+delValues);
        StringTokenizer st = new StringTokenizer(delValues,",");
        List<Long> ids = new ArrayList<>();
        while (st.hasMoreTokens()){
            String token = st.nextToken().trim();
            System.out.println("TOKEN => " + token);
            System.out.println("Long.parseLong(token) => " + Long.parseLong(token));
            ids.add(Long.parseLong(token));
        }
        fileRepository.deleteAllByIdInBatch(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public void multiDelete(Long id, String board) {
        fileRepository.multiDelete(id,board);
    }


}
