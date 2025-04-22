package com.hrbank.storage;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "hrbank.storage.type", value = "local")
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
        throw new RuntimeException(e);
      }
    }
  }

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

}
