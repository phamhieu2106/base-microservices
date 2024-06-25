package org.example.insuranceservice.domain.response;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InsuranceResponse {
    String id;
    String insuranceCode;
    String insuranceName;
    Double totalPaymentFeeAmount;
    Double totalInsuranceTotalFeeAmount;
}
