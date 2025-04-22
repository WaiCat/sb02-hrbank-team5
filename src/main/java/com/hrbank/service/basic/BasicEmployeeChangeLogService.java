package com.hrbank.service.basic;

import com.hrbank.dto.employeeChangeLog.EmployeeChangeLogSearchRequest;
import com.hrbank.entity.EmployeeChangeLog;
import com.hrbank.repository.EmployeeChangeLogRepository;
import com.hrbank.repository.specification.EmployeeChangeLogSpecification;
import com.hrbank.service.EmployeeChangeLogService;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BasicEmployeeChangeLogService implements EmployeeChangeLogService {

  private final EmployeeChangeLogRepository repository;

  @Override
  public Page<EmployeeChangeLog> searchLogs(EmployeeChangeLogSearchRequest request, Pageable pageable) {
    Specification<EmployeeChangeLog> spec = Specification.<EmployeeChangeLog>where(null)
        .and(EmployeeChangeLogSpecification.employeeNumberContains(request.getEmployeeNumber()))
        .and(EmployeeChangeLogSpecification.memoContains(request.getMemo()))
        .and(EmployeeChangeLogSpecification.ipAddressContains(request.getIpAddress()))
        .and(EmployeeChangeLogSpecification.typeEquals(request.getType()))
        .and(EmployeeChangeLogSpecification.atBetween(request.getAtFrom(), request.getAtTo()));

    return repository.findAll(spec, pageable);
  }

  @Override
  public Optional<EmployeeChangeLog> findWithDetailsById(UUID id) {
    return repository.findById(id); // 추후 fetch join 필요하면 custom query로 변경
  }
}

