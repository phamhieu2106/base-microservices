package org.example.insuranceservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "insurance", indexes = {
        @Index(name = "idx_insurance_code", columnList = "insuranceCode"),
        @Index(name = "idx_insurance_name", columnList = "insuranceName"),
        @Index(name = "idx_insurance_name_and_code", columnList = "insuranceName, insuranceCode"),
        @Index(name = "idx_insurance_created_at", columnList = "createdAt")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsuranceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String insuranceCode;

    String insuranceName;

    Double totalPaymentFeeAmount;

    Double totalInsuranceTotalFeeAmount;

    Boolean softDelete = false;

    Date createdAt;

    String createdBy;

    Date updatedAt;

    String lastUpdatedBy;
}
