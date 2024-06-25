package org.example.customerservice.domain.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.sharedlibrary.constaint.PageConstant;
import org.example.sharedlibrary.request.PageRequest;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageCustomerRequest extends PageRequest {
    String statusCustomer = PageConstant.PAGE_DEFAULT_VALUE;
}