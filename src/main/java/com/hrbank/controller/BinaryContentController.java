package com.hrbank.controller;

import com.hrbank.controller.api.BinaryContentApi;
import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.service.BinaryContentService;
import com.hrbank.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @Override
  @GetMapping("/{id}/download")
  public ResponseEntity<Resource>download(@PathVariable("id") Long id) {
    BinaryContentDto contentDto = binaryContentService.findById(id);
    return binaryContentStorage.download(contentDto);
  }
}
