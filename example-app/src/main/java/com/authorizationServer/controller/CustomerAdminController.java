package com.authorizationServer.controller;

import com.authorizationServer.dto.CustomerCreateRequest;
import com.authorizationServer.dto.CustomerResponse;
import com.authorizationServer.services.CustomerManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
public class CustomerAdminController {

    private final CustomerManagementService customerManagementService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerManagementService.createCustomer(request));
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerManagementService.getAllCustomers();
    }

    @GetMapping("/{email}")
    public CustomerResponse getCustomerByEmail(@PathVariable String email) {
        return customerManagementService.getCustomerByEmail(email);
    }
}
