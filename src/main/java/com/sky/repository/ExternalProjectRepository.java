package com.sky.repository;

import com.sky.model.entity.ExternalProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalProjectRepository extends JpaRepository<ExternalProject, String> {
  List<ExternalProject> findByUserId(Long userId);

  Optional<ExternalProject> findByNameAndUserId(String name, Long userId);
}
