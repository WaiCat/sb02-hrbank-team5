package com.hrbank.service;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.entity.BinaryContent;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.storage.BinaryContentStorage;
import io.swagger.v3.oas.annotations.servers.Server;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Server
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @Transactional
  public BinaryContent create(BinaryContentCreateRequest request) {
    String originalFilename = request.file().getOriginalFilename();
    String contentType = request.file().getContentType();
    long size = request.file().getSize();
    byte[] bytes;
    try {
      bytes = request.file().getBytes();
    } catch (IOException e) {
      throw new RuntimeException("파일 변환 중 오류 발생: " + e);
    }
    BinaryContent binaryContent = new BinaryContent(originalFilename, contentType, size);
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    return binaryContent;
  }

}
