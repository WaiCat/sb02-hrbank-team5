package com.hrbank.service.basic;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.entity.BinaryContent;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import com.hrbank.mapper.BinaryContentMapper;
import com.hrbank.repository.BinaryContentRepository;
import com.hrbank.service.BinaryContentService;
import com.hrbank.storage.BinaryContentStorage;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
      log.error("파일의 byte[]를 가져오는 도중 에러 발생 : {}", e.getMessage());
      throw new RestException(ErrorCode.FILE_READ_ERROR);
    }
    BinaryContent binaryContent = new BinaryContent(originalFilename, contentType, size);
    binaryContentRepository.save(binaryContent);
    binaryContentStorage.put(binaryContent.getId(), bytes);
    return binaryContent;
  }

  @Override
  public BinaryContentDto findById(Long id){
    BinaryContent binaryContent = binaryContentRepository.findById(id)
        .orElseThrow(() -> new RestException(ErrorCode.PROFILE_IMAGE_NOT_FOUND));
    return binaryContentMapper.toDto(binaryContent);
  }

  @Override
  @Transactional
  public void delete(Long id) {
    if (!binaryContentRepository.existsById(id)) {
      throw new RestException(ErrorCode.PROFILE_IMAGE_NOT_FOUND);
    }
    binaryContentStorage.delete(id);
    binaryContentRepository.deleteById(id);
  }
}
