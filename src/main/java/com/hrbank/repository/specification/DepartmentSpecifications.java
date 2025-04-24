package com.hrbank.repository.specification;

import com.hrbank.entity.Department;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class DepartmentSpecifications {
    public static Specification<Department> nameOrDescriptionContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), likePattern),
                    cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    public static Specification<Department> idGreaterThan(Long lastId) {
        return (root, query, cb) -> lastId == null ? null : cb.greaterThan(root.get("id"), lastId);
    }

    //정렬 필드에 따른 커서 조건 생성
    public static Specification<Department> cursorCondition(
        String sortField,
        String sortDirection,
        String cursor,
        Long idAfter
    ) {
        return (root, query, cb) -> {
            if (cursor == null && idAfter == null) return null;

            boolean isAscending = "asc".equalsIgnoreCase(sortDirection);

            if ("name".equals(sortField)) {
                // 이름 기준 커서
                if (cursor != null) {
                    Predicate namePredicate = isAscending
                            ? cb.greaterThan(root.get("name"), cursor)
                            : cb.lessThan(root.get("name"), cursor);

                    // 이름이 같은 경우 ID로 추가 정렬
                    Predicate nameEqualAndIdPredicate = cb.and(
                            cb.equal(root.get("name"), cursor),
                            isAscending
                                    ? cb.greaterThan(root.get("id"), idAfter != null ? idAfter : 0L)
                                    : cb.lessThan(root.get("id"), idAfter != null ? idAfter : Long.MAX_VALUE)
                    );

                    return cb.or(namePredicate, nameEqualAndIdPredicate);
                }
            } else if ("establishedDate".equals(sortField)) {
                // 설립일 기준 커서
                if (cursor != null) {
                    try {
                        LocalDate cursorDate = LocalDate.parse(cursor);
                        Predicate datePredicate = isAscending
                                ? cb.greaterThan(root.get("establishedDate"), cursorDate)
                                : cb.lessThan(root.get("establishedDate"), cursorDate);

                        // 날짜가 같은 경우 ID로 추가 정렬
                        Predicate dateEqualAndIdPredicate = cb.and(
                                cb.equal(root.get("establishedDate"), cursorDate),
                                isAscending
                                        ? cb.greaterThan(root.get("id"), idAfter != null ? idAfter : 0L)
                                        : cb.lessThan(root.get("id"), idAfter != null ? idAfter : Long.MAX_VALUE)
                        );

                        return cb.or(datePredicate, dateEqualAndIdPredicate);
                    } catch (Exception e) {
                        // 날짜 파싱 오류 시 ID 기반으로 fallback
                        return idGreaterThan(idAfter).toPredicate(root, query, cb);
                    }
                }
            }
            // cursor가 없거나 지원하지 않는 정렬 필드인 경우 ID 기반 fallback
            return idGreaterThan(idAfter).toPredicate(root, query, cb);
        };
    }

    //검색 조건과 커서 조건 조합
    public static Specification<Department> buildSearchSpecification(
            String nameOrDescription,
            Long idAfter,
            String cursor,
            String sortField,
            String sortDirection
    ) {
        return Specification
                .where(nameOrDescriptionContains(nameOrDescription))
                .and(cursorCondition(sortField, sortDirection, cursor, idAfter));
    }
}

