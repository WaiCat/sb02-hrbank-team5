package com.hrbank.dto.binarycontent;

import org.springframework.web.multipart.MultipartFile;

public record BinaryContentCreateRequest(
    MultipartFile file
) {

  public static BinaryContentCreateRequest of(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    return new BinaryContentCreateRequest(file);
  }
}