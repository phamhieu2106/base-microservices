package org.example.customerservice.service;

import org.example.customerservice.domain.request.CustomerAddRequest;
import org.example.customerservice.domain.request.CustomerUpdateRequest;
import org.example.customerservice.domain.request.PageCustomerRequest;
import org.example.customerservice.domain.response.customer.CustomerResponse;
import org.example.sharedlibrary.response.WrapperResponse;

public interface CustomerService extends IService<CustomerAddRequest, CustomerUpdateRequest> {
    WrapperResponse findAllCustomer(PageCustomerRequest request);

    boolean isCustomerExits(String id);

    CustomerResponse findCustomerById(String id);
}
