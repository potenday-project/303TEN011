package com.beside.ten011.archive.repository;

import com.beside.ten011.archive.entity.Archive;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;


public class ArchiveSpec {
    public static Specification<Archive> searchWith(final Long userId, final String title,
                                                    final Integer year, final Integer month) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(title)) {
                predicates.add(builder.like(root.get("title"), "%" + title + "%"));
            }
            if (userId != null) {
                predicates.add(builder.equal(root.get("user"), userId));
            }
            if (year != null) {
                if (month != null) {
                    LocalDate date = LocalDate.of(year, month, 1);
                    LocalDate lastDate = YearMonth.from(date).atEndOfMonth();
                    predicates.add(builder.between(root.get("createdDt"), date, lastDate));
                } else {
                    LocalDate date = LocalDate.of(year, 1, 1);
                    LocalDate lastDate = LocalDate.of(year, 12, 31);
                    predicates.add(builder.between(root.get("createdDt"), date, lastDate));
                }
            }
            query.distinct(true);
            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
