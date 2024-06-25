package org.example.contractservice.domain.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.sharedlibrary.constaint.PageConstant;
import org.example.sharedlibrary.request.PageRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractPageRequest extends PageRequest {
    String statusPayment = PageConstant.PAGE_DEFAULT_VALUE;
    String statusContract = PageConstant.PAGE_DEFAULT_VALUE;
}
