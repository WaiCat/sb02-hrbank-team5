package com.hrbank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "binary_contents")
public class BinaryContent {

  @Id
  @GeneratedValue
  private Long id;

  @Setter
  @Column(name = "file_name", nullable = false)
  private String fileName;

  @Setter
  @Column(name = "content_type", nullable = false)
  private String contentType;

  @Setter
  @Column(nullable = false)
  private Long size;

  public BinaryContent(String fileName, String contentType, Long size) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.size = size;
  }
}