package com.hrbank.service;

import com.hrbank.dto.binarycontent.BinaryContentCreateRequest;
import com.hrbank.entity.BinaryContent;

public interface BinaryContentService {
  BinaryContent create(BinaryContentCreateRequest request);

  //BinaryContentDto findById(Long id);

  //void delete(Long id);
}
