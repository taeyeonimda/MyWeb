package com.MyWeb.File.repository;

import com.MyWeb.File.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File,Long> {
    List<File> findAllByRefId(Long refId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM File f WHERE f.refId = :refId AND f.type = :type")
    void multiDelete(@Param("refId") Long refId, @Param("type") String type);
}
