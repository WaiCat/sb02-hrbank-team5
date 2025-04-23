package com.hrbank.storage;

import com.hrbank.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", value = "local")
public class BinaryContentStorage {
  private final Path root;
  private static final Logger log = LoggerFactory.getLogger(BinaryContentStorage.class);

  public BinaryContentStorage(@Value(".hrbank/storage") Path root){
    this.root = root;
  }

  @PostConstruct
  public void init(){
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  // 프로필 이미지 파일 Storage에 저장
  public void put(Long id, byte[] data){
    Path filePath = root.resolve(id.toString());
    if (Files.exists(filePath)) {
      throw new RuntimeException("이미 존재하는 파일");
    }
    try (OutputStream fos = Files.newOutputStream(filePath)) {
      fos.write(data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // csv 파일 저장
  public void putCsvFile(Long id, File file) throws IOException {
    Path filePath = root.resolve(id + ".csv");
    Files.copy(file.toPath(), filePath, StandardCopyOption.REPLACE_EXISTING);
  }

  // log 파일 저장
  public void putErrorLog(Long id, String log) throws IOException {
    Path logPath = root.resolve(id + ".log");
    Files.writeString(logPath, log, StandardCharsets.UTF_8);
  }

  // id로 실제 파일 찾기
  public InputStream get(Long id){
    Path filePath = root.resolve(id.toString());
    if (!Files.exists(filePath)) {
      throw new RuntimeException("존재하지 않는 파일");
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 중 실패: " + e);
    }
  }

  // 데이터 백업 과정이 실패하였을 때, 저장한 csv 파일 삭제
  public void deleteCsvFile(Long id) throws IOException {
    Path target = root.resolve(id + ".csv");
    Files.deleteIfExists(target);
  }

  // 파일 다운로드 - .csv, .log 컨텐트 타입 인식되도록 함.
  public ResponseEntity<Resource> download(BinaryContentDto contentDto) {
    InputStream downloadData = get(contentDto.id());
    Resource resource = new InputStreamResource(downloadData);

    String contentTypeStr = contentDto.contentType();
    MediaType mediaType;

    if(contentTypeStr != null && !contentTypeStr.isBlank()) {
      try{
        mediaType = MediaType.parseMediaType(contentTypeStr);
      }catch (Exception e){
        log.warn("DTO에 저장된 contentType 값이 유효하지 않음: '{}'. application/octet-stream 으로 대체.", contentTypeStr, e);
        mediaType = MediaType.APPLICATION_OCTET_STREAM;
      }
    }else {
      mediaType = MediaType.APPLICATION_OCTET_STREAM;
    }

    return ResponseEntity.ok()
        .contentType(mediaType)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + contentDto.fileName() + "\"")
        .body(resource);
  }

}
