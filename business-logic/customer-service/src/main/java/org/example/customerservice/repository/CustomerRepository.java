package org.example.customerservice.repository;

import org.example.customerservice.domain.entity.CustomerEntity;
import org.example.customerservice.domain.model.IdentityModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, String>, JpaSpecificationExecutor<CustomerEntity> {


    Optional<CustomerEntity> findCustomerByIdAndSoftDeleteIsFalse(String id);

    boolean existsByCustomerCode(String customerCode);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsCustomerByProof(IdentityModel proof);

    boolean existsByEmailAndIdIsNot(String email, String Id);

    boolean existsByPhoneNumberAndIdIsNot(String phoneNumber, String id);

    boolean existsCustomerByProofAndIdIsNot(IdentityModel proof, String id);

    long count();
}
