package com.MyWeb.File.service;

import com.MyWeb.File.entity.File;
import com.MyWeb.File.repository.FileRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.expression.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void FileSaves(){

    }

    public File save(File entityFile) {
        return fileRepository.save(entityFile);
    }

    public List<File> getAllFile(Long id) {
        return fileRepository.findAllByRefId(id);
    }
}
