package com.customer.controller;

import com.customer.model.dto.ApiResponse;
import com.customer.model.dto.CustomerDTO;
import com.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse> createCustomer(@Valid @RequestBody CustomerDTO customer) {
        return service.save(customer);
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getCustomers() {
        return service.getAllCustomers();
    }
}
