package org.example.contractservice.repository;

import jakarta.transaction.Transactional;
import org.example.contractservice.domain.entity.ContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<ContractEntity, String>,
        JpaSpecificationExecutor<ContractEntity> {

    boolean existsByContractCode(String contractCode);

    List<ContractEntity> findAllByCustomerIdAndSoftDeleteIsFalse(String customerId);

    Optional<ContractEntity> findByIdAndSoftDeleteIsFalse(String contractId);

    @Query(value = """
             UPDATE ContractEntity c
             SET c.statusContract = "EFFECTED"
             WHERE c.statusContract = "NOT_EFFECT"
             AND :now >= c.contractStartDate AND :now <= c.contractEndDate
            """)
    @Modifying
    @Transactional
    void updateStatusContractNotEffect(Date now);

    @Query(value = """
             UPDATE ContractEntity c
             SET c.statusContract = "END_EFFECTED"
             WHERE c.statusContract = "EFFECTED"
             AND :now > c.contractEndDate
            """)
    @Modifying
    @Transactional
    void updateStatusContractEffected(Date now);
}
