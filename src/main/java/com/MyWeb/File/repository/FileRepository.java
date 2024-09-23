package com.MyWeb.File.repository;

import com.MyWeb.File.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {
    List<File> findAllByRefId(Long refId);
}
