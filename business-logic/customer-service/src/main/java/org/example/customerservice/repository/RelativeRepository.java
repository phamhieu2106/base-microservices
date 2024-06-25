package org.example.customerservice.repository;

import org.example.customerservice.domain.entity.RelativeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelativeRepository extends JpaRepository<RelativeEntity, String> {

    List<RelativeEntity> findAllByCustomerIdAndSoftDeleteIsFalse(String customerId);

    Optional<RelativeEntity> findByCustomerIdAndRelativeNameAndSoftDeleteIsFalse(String customerId, String relativeName);
}
