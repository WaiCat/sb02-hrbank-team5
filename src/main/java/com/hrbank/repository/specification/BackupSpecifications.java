package com.hrbank.repository.specification;

import com.hrbank.entity.Backup;
import com.hrbank.enums.BackupStatus;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class BackupSpecifications {

  public static Specification<Backup> buildSearchSpecification(
      String worker,
      BackupStatus status,
      LocalDateTime from,
      LocalDateTime to,
      Long idAfter,
      String sortDirection
  ) {
    return (root, query, cb) -> {
      Predicate predicate = cb.conjunction(); // 기본 true

      // worker LIKE 검색
      if (worker != null && !worker.isBlank()) {
        predicate = cb.and(predicate,
            cb.like(cb.lower(root.get("worker")), "%" + worker.toLowerCase() + "%"));
      }

      // status = 검색
      if (status != null) {
        predicate = cb.and(predicate,
            cb.equal(root.get("status"), status));
      }

      // from 이후
      if (from != null) {
        predicate = cb.and(predicate,
            cb.greaterThanOrEqualTo(root.get("startedAt"), from));
      }

      // to 이전
      if (to != null) {
        predicate = cb.and(predicate,
            cb.lessThanOrEqualTo(root.get("startedAt"), to));
      }

      // idAfter 값을 기준으로 필터링 (정렬 방향에 따라 조건을 다르게 설정)
      if (idAfter != null) {
        if ("ASC".equalsIgnoreCase(sortDirection)) {
          // 오름차순에서는 id > idAfter 조건
          predicate = cb.and(predicate, cb.greaterThan(root.get("id"), idAfter));
        } else if ("DESC".equalsIgnoreCase(sortDirection)) {
          // 내림차순에서는 id < idAfter 조건
          predicate = cb.and(predicate, cb.lessThan(root.get("id"), idAfter));
        }
      }

      return predicate;
    };
  }
}
