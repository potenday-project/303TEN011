package com.beside.ten011.archive.repository;

import com.beside.ten011.archive.entity.Archive;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class ArchiveSpec {
    public static Specification<Archive> searchWith(final Long userId, final String title) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            }
            if (userId != null) {
                predicates.add(builder.equal(root.get("user"), userId));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
