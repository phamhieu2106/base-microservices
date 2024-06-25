package org.example.contractservice.specific;

import org.example.contractservice.domain.entity.ContractEntity;
import org.springframework.data.jpa.domain.Specification;

public class ContractSpecifications {

    public static final String CONTRACT_CODE = "contractCode";
    public static final String CONTRACT_PAYMENT = "statusPayment";
    public static final String CONTRACT_STATUS = "statusContract";
    public static final String SOFT_DELETE = "softDelete";

    public static Specification<ContractEntity> withKeywordAndStatus(
            String keyword, String statusPayment, String statusContract
    ) {
        Specification<ContractEntity> spec = Specification.where(isSoftDeleteFalse());

        if (!keyword.isBlank()) spec = spec.and(withKeyword(keyword));
        if (!statusPayment.isBlank()) spec = spec.and(withStatusPayment(statusPayment));
        if (!statusContract.isBlank()) spec = spec.and(withStatusContract(statusContract));

        return spec;
    }

    private static Specification<ContractEntity> withKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(
                                root.get(CONTRACT_CODE)), "%" + keyword.toLowerCase() + "%"));
    }

    private static Specification<ContractEntity> withStatusPayment(String statusPayment) {
        if (statusPayment == null || statusPayment.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CONTRACT_PAYMENT), statusPayment);
    }

    private static Specification<ContractEntity> withStatusContract(String statusContract) {
        if (statusContract == null || statusContract.isEmpty()) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(CONTRACT_STATUS), statusContract);
    }

    public static Specification<ContractEntity> isSoftDeleteFalse() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get(SOFT_DELETE));
    }
}
