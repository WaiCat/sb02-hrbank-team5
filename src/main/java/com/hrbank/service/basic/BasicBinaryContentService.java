package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.entity.BinaryContent;
import com.hrbank.mapper.BinaryContentMapper;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.service.BinaryContentService;
import com.hrbank.storage.BinaryContentStorage;
import java.io.IOException;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicBinaryContentService implements BinaryContentService {
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

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

  @Override
  public BinaryContentDto findById(Long id){
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementException(id + " 에 해당하는 BinaryContent를 찾을 수 없음"));
    return binaryContentMapper.toDto(binaryContent);
  }

}
