package com.orbis.stream.controller.filter;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DynamicSpecificationBuilder {

    private DynamicSpecificationBuilder() {}

    public static <T> Specification<T> buildSpecification(Map<String, String> filters){

        return (root, query, criteriaBuilder) -> {

            List<Predicate> filteringPredicates = new ArrayList<>();

            filters.forEach((fieldName, fieldValue) -> {

                Predicate fieldPredicate = criteriaBuilder.equal(
                        root.get(fieldName),
                        fieldValue
                );

                filteringPredicates.add(fieldPredicate);
            });

            return criteriaBuilder.and(filteringPredicates.toArray(new Predicate[0]));
        };
    }
}