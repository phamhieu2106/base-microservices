package org.example.contractservice.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.contractservice.domain.model.InsuranceModel;
import org.example.contractservice.enumeration.StatusContract;
import org.example.contractservice.enumeration.StatusPayment;
import org.example.sharedlibrary.converter.ModelJsonAttributeConverter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "contract",
        indexes = {
                @Index(name = "idx_contract_code", columnList = "contractCode"),
                @Index(name = "idx_contract_customer_id", columnList = "customerId"),
                @Index(name = "idx_contract_payment_status", columnList = "statusPayment"),
                @Index(name = "idx_contract_status", columnList = "statusContract"),
                @Index(name = "idx_contract_code_cusId_statusP_statusC"
                        , columnList = "contractCode,customerId,statusPayment,statusContract"),
                @Index(name = "idx_contract_created_at", columnList = "createdAt")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String contractCode;

    Date contractStartDate;

    Date contractEndDate;

    Double contractTotalPayAmount; //tổng phí bảo hiểm hợp đồng

    Double contractTotalInsurancePayAmount; //tổng phí loại hình

    Double contractTotalNeedPayAmount; //tổng phí cần thanh toán

    Double contractTotalPayedAmount; //tổng phí đã thanh toán

    String customerId;

    @Convert(converter = ModelJsonAttributeConverter.class)
    @Column(length = 10000)
    List<InsuranceModel> insuranceEntities;

    @Enumerated(EnumType.STRING)
    StatusPayment statusPayment;

    @Enumerated(EnumType.STRING)
    StatusContract statusContract;

    Boolean softDelete = false;

    Date createdAt;

    String createdBy;

    Date updatedAt;

    String lastUpdatedBy;

}
