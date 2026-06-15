package com.customer.service;

import com.customer.model.dto.ApiResponse;
import com.customer.model.dto.CustomerDTO;
import com.customer.model.entity.Customer;
import com.customer.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    public ResponseEntity<ApiResponse> save(CustomerDTO customerDTO) {
        Customer customer = customerDTO.toEntity();
        try {
            customer = repository.save(customer);
            return ResponseEntity.ok(new ApiResponse(200, "Customer saved successfully", customer));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // let GlobalExceptionHandler map integrity violations to a validation-like response
            throw e;
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(500, "Error saving customer: " + e.getMessage(), null));
        }
    }

    public ResponseEntity<ApiResponse> getAllCustomers() {
        try {
            List<Customer> customers = repository.findAll();
            
            return ResponseEntity.ok(new ApiResponse(200, "Customers retrieved successfully", customers));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(500, "Error retrieving customers: " + e.getMessage(), null));
        }
    }
}
