package com.hrbank.storage;

import com.hrbank.dto.binarycontent.BinaryContentDto;
import com.hrbank.exception.ErrorCode;
import com.hrbank.exception.RestException;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "hrbank.storage.type", havingValue = "local")
public class BinaryContentStorage {
  private final Path root;

  public BinaryContentStorage(@Value(".hrbank/storage") Path root){
    this.root = root;
  }

  @PostConstruct
  public void init(){
    if (!Files.exists(root)) {
      try {
        Files.createDirectories(root);
      } catch (IOException e) {
        throw new RestException(ErrorCode.STORAGE_INITIALIZATION_FAILED);
      }
    }
  }

  // 프로필 이미지 파일 Storage에 저장
  public void put(Long id, byte[] data, String subtype) {
    Path filePath = root.resolve(id.toString() + "." + subtype);

    if (Files.exists(filePath)) {
      throw new RestException(ErrorCode.FILE_ALREADY_EXIST);
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
  public Long putErrorLog(Long id, String log) throws IOException {
    Path logPath = root.resolve(id + ".log");
    Files.writeString(logPath, log, StandardCharsets.UTF_8);
    return Files.size(logPath);
  }

  // id로 실제 파일 찾기
  public InputStream get(Long id, String subtype){
    Path filePath = root.resolve(id.toString() + "." + subtype);
    if (!Files.exists(filePath)) {
      throw new RestException(ErrorCode.FILE_NOT_FOUND);
    }
    try {
      return Files.newInputStream(filePath);
    } catch (IOException e) {
      throw new RestException(ErrorCode.FILE_READ_ERROR);
    }
  }

  // 프로필 이미지 삭제
  public void delete(Long id, String subtype){
    Path filePath = root.resolve(id.toString() + "." + subtype);
    try{
      boolean success = Files.deleteIfExists(filePath);
      if(!success){
        throw new RestException(ErrorCode.FILE_NOT_FOUND);
      }
    }catch (IOException e){
      log.error("파일 삭제 중 오류 발생 (filePath={})", filePath, e);
      throw new RestException(ErrorCode.FILE_DELETE_ERROR);
    }
  }

  // 데이터 백업 과정이 실패하였을 때, 저장한 csv 파일 삭제
  public void deleteCsvFile(Long id) throws IOException {
    Path target = root.resolve(id + ".csv");
    Files.deleteIfExists(target);
  }

  // 파일 다운로드 - .csv, .log 컨텐트 타입 인식되도록 함.
  public ResponseEntity<Resource> download(BinaryContentDto contentDto) {
    String contentTypeStr = contentDto.contentType();
    String subtype = "";
    if (contentTypeStr != null) {
      subtype = contentTypeStr.substring(contentTypeStr.indexOf('/') + 1);
    }
    InputStream downloadData = get(contentDto.id(), subtype);
    Resource resource = new InputStreamResource(downloadData);

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
            buildContentDisposition(contentDto.fileName()))
        .body(resource);
  }

  private String buildContentDisposition(String fileName) {
    try {
      String encoded = URLEncoder
          .encode(fileName, StandardCharsets.UTF_8)
          .replaceAll("\\+", "%20");

      String fallback = fileName.replaceAll("[^A-Za-z0-9._-]", "_");

      return "attachment; filename=\""
          + fallback
          + "\"; filename*=UTF-8''"
          + encoded;
    } catch (Exception e) {
      return "attachment";
    }
  }
}
