package org.example.customerservice.specific;

import org.example.customerservice.domain.entity.CustomerEntity;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecifications {

    public static final String CUSTOMER_CODE = "customerCode";
    public static final String CUSTOMER_NAME = "customerName";
    public static final String CUSTOMER_STATUS = "statusCustomer";
    public static final String SOFT_DELETE = "softDelete";

    public static Specification<CustomerEntity> withKeywordAndStatus(String keyword, String status) {
        Specification<CustomerEntity> spec = Specification.where(isSoftDeleteFalse());

        if (!keyword.isBlank()) spec = spec.and(withKeyword(keyword));
        if (!status.isBlank()) spec = spec.and(withStatus(status));

        return spec;
    }

    private static Specification<CustomerEntity> withKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(CUSTOMER_CODE)), "%" + keyword.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get(CUSTOMER_NAME)), "%" + keyword.toLowerCase() + "%")
                );
    }

    private static Specification<CustomerEntity> withStatus(String statusCustomer) {
        if (statusCustomer == null || statusCustomer.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CUSTOMER_STATUS), statusCustomer);
    }

    public static Specification<CustomerEntity> isSoftDeleteFalse() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get(SOFT_DELETE));
    }

}
