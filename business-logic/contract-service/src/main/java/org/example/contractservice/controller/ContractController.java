package org.example.contractservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.contractservice.domain.request.ContractAddRequest;
import org.example.contractservice.domain.request.ContractPageRequest;
import org.example.contractservice.domain.request.ContractUpdateRequest;
import org.example.contractservice.service.ContractService;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;


    @GetMapping
    public WrapperResponse getContracts(
            @RequestBody ContractPageRequest request) {
        return contractService.findAll(request);
    }

    @GetMapping("/{id}")
    public WrapperResponse getContractById(@PathVariable String id) {
        return contractService.find(id);
    }

    @GetMapping("/find-by-customerId/{id}")
    public WrapperResponse getContractByCustomerId(@PathVariable String id) {
        return contractService.findAllByCustomerId(id);
    }

    @PostMapping("/create")
    public WrapperResponse addContract(@Valid @RequestBody ContractAddRequest request) {
        return contractService.add(request);
    }

    @PostMapping("detail/{id}")
    public WrapperResponse updateContract(@Valid @RequestBody ContractUpdateRequest request,
                                          @PathVariable String id) {
        return contractService.update(request, id);
    }

    @PostMapping("delete/{id}")
    public WrapperResponse deleteContract(@PathVariable String id) {
        return contractService.delete(id);
    }

    @PostMapping("/cancel-contract/{id}")
    public WrapperResponse cancelContract(@PathVariable String id) {
        return contractService.cancelContract(id);
    }
}
