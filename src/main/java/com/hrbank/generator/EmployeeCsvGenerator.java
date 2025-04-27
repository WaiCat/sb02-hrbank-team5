package com.hrbank.generator;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Employee;
import com.hrbank.enums.EmployeeCsvHeader;
import com.hrbank.repository.EmployeeRepository;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EmployeeCsvGenerator {
  private final EmployeeRepository employeeRepository;

  // csv 파일에 전체 직원 데이터 넣음. (OOM 방지 & 대량 데이터을 생각하여 커서 사용)
  @Transactional(
      propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ, readOnly = true
  )
  public File generate(BackupDto dto) throws IOException {
    // 혹시 모르니 임시 파일도 유니크한 파일명으로 저장되도록 함.
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    String timestamp = fmt.format(dto.startedAt());
    String backupId = String.valueOf(dto.id());
    File tempFile = File.createTempFile(backupId + "_" + timestamp,".csv");

    tempFile.deleteOnExit(); // JVM 종료 시 임시 파일 삭제(이중 대책)

    try (
        var writer = Files.newBufferedWriter(tempFile.toPath(), StandardCharsets.UTF_8);
        CSVPrinter printer = new
            CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
            EmployeeCsvHeader.class))
    ) {
      final int chunkSize = 100;
      Long lastId = 0L;
      List<Employee> chunk;
      do {
        chunk = employeeRepository.findNextChunk(lastId, PageRequest.of(0, chunkSize));
        for (Employee e : chunk) {
          printer.printRecord(
              e.getId(),
              e.getEmployeeNumber(),
              e.getName(),
              e.getEmail(),
              e.getDepartment() != null ? e.getDepartment().getName() : "",
              e.getPosition(),
              e.getHireDate().toString(),
              e.getStatus()
          );
          lastId = e.getId();
        }
      } while (!chunk.isEmpty());
      printer.flush();
    }
    return tempFile;
  }
}
