package com.hrbank.repository.specification;

import com.hrbank.entity.Department;
import org.springframework.data.jpa.domain.Specification;

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

    public static Specification<Department> buildSearchSpecification(
            String nameOrDescription,
            Long idAfter
    ) {
        return Specification
                .where(nameOrDescriptionContains(nameOrDescription))
                .and(idGreaterThan(idAfter));
    }
}

