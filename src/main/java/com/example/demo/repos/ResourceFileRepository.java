package com.example.demo.repos;

import com.example.demo.model.ResourceFile;
import com.example.demo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceFileRepository extends JpaRepository<ResourceFile, Integer> {
    ResourceFile findFirstByMd5Value(String md5Value);

    ResourceFile getById(Integer id);
}
