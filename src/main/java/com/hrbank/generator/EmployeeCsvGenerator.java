package com.hrbank.generator;

import com.hrbank.dto.backup.BackupDto;
import com.hrbank.entity.Employee;
import com.hrbank.enums.EmployeeCsvHeader;
import com.hrbank.repository.EmployeeRepository;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmployeeCsvGenerator {
  private final EmployeeRepository employeeRepository;

  // csv 파일에 전체 직원 데이터 넣음. (OOM 방지를 위해 페이징 사용)
  public File generate(BackupDto dto) throws IOException {
    DateTimeFormatter fmt = DateTimeFormatter
        .ofPattern("yyyyMMdd_HHmmss")
        .withZone(ZoneId.of("Asia/Seoul"));
    String timestamp = fmt.format(dto.startedAt());
    String backupId = String.valueOf(dto.id());

    File tempFile = File.createTempFile(
        "employee_backup_" + backupId + "_" + timestamp,".csv");

    tempFile.deleteOnExit(); // JVM 종료 시 임시 파일 삭제(이중 대책임)

    try (
        var writer = Files.newBufferedWriter(tempFile.toPath(), StandardCharsets.UTF_8);
        CSVPrinter printer = new
            CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
            EmployeeCsvHeader.class))
    ) {
      int page = 0;
      Page<Employee> chunk;
      do {
        chunk = employeeRepository.findAll(
            PageRequest.of(page++, 100, Sort.by("id").ascending())
        );
        for (Employee e : chunk.getContent()) {
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
        }
      } while (chunk.hasContent());
      printer.flush();
    }
    return tempFile;
  }
}
