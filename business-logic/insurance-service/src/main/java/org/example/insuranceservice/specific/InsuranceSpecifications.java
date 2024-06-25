package org.example.insuranceservice.specific;

import org.example.insuranceservice.domain.entity.InsuranceEntity;
import org.springframework.data.jpa.domain.Specification;

public class InsuranceSpecifications {

    public static final String INSURANCE_CODE = "insuranceCode";
    public static final String INSURANCE_NAME = "insuranceName";
    public static final String SOFT_DELETE = "softDelete";

    public static Specification<InsuranceEntity> withSpec(String keyword) {
        Specification<InsuranceEntity> spec = Specification.where(isSoftDeleteFalse());

        if (!keyword.isBlank()) spec = spec.and(withKeyword(keyword));

        return spec;
    }

    private static Specification<InsuranceEntity> withKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(INSURANCE_CODE))
                                , "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(INSURANCE_NAME))
                                , "%" + keyword.toLowerCase() + "%")
                );
    }

    public static Specification<InsuranceEntity> isSoftDeleteFalse() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get(SOFT_DELETE));
    }
}
