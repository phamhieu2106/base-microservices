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
public class ContractUpdateRequest {
    String customerId;
    Date contractStartDate;
    Date contractEndDate;
    List<String> insurancesId;
    Double contractTotalPayedAmount;
}
