package com.sky.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "external_projects", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "name"})})
@Data
public class ExternalProject {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_project_seq")
  @SequenceGenerator(name = "external_project_seq", sequenceName = "external_project_id_seq", allocationSize = 1)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(nullable = false)
  private String name;
}
