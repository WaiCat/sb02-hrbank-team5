package com.hrbank.repository;

import com.hrbank.entity.EmployeeChangeLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EmployeeChangeLogRepository extends JpaRepository<EmployeeChangeLog, UUID>, JpaSpecificationExecutor<EmployeeChangeLog> {
}
