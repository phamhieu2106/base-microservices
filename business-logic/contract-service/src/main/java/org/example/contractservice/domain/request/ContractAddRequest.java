package org.example.contractservice.domain.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractAddRequest {
    String customerId;
    Date contractStartDate;
    Date contractEndDate;
    Double contractTotalPayedAmount;
    List<String> insurancesId;

}
