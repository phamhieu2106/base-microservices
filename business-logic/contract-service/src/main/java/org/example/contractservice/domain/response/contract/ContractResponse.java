package org.example.contractservice.domain.response.contract;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.contractservice.domain.model.InsuranceModel;
import org.example.contractservice.domain.response.customer.CustomerResponse;
import org.example.contractservice.enumeration.StatusContract;
import org.example.contractservice.enumeration.StatusPayment;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {

    String id;
    String contractCode;
    Date contractStartDate;
    Date contractEndDate;
    Double contractPayAmount;
    Double contractInsurancePayAmount;
    Double contractNeedPayAmount;
    CustomerResponse customer;
    StatusPayment statusPayment;
    StatusContract statusContract;
    List<InsuranceModel> insuranceEntities;
}
