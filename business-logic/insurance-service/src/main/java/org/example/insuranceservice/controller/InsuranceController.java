package org.example.insuranceservice.controller;

import jakarta.validation.Valid;
import org.example.insuranceservice.domain.request.InsuranceAddRequest;
import org.example.insuranceservice.domain.request.InsurancePageRequest;
import org.example.insuranceservice.domain.request.InsuranceUpdateRequest;
import org.example.insuranceservice.service.InsuranceService;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insurances")
public class InsuranceController {

    private final InsuranceService insuranceService;

    @Autowired
    public InsuranceController(InsuranceService insuranceService) {
        this.insuranceService = insuranceService;
    }

    @GetMapping
    public WrapperResponse findAll(@RequestBody InsurancePageRequest request) {
        return insuranceService.findAll(request);
    }

    @GetMapping("detail/{id}")
    public WrapperResponse findById(@PathVariable String id) {
        return insuranceService.find(id);
    }

    @PostMapping("/create")
    public WrapperResponse add(@Valid @RequestBody InsuranceAddRequest request) {
        return insuranceService.add(request);
    }

    @PostMapping("update/{id}")
    public WrapperResponse update(@Valid @RequestBody InsuranceUpdateRequest request,
                                  @PathVariable String id) {
        return insuranceService.update(request, id);
    }

    @PostMapping("delete/{id}")
    public WrapperResponse delete(@PathVariable String id) {
        return insuranceService.delete(id);
    }

}
