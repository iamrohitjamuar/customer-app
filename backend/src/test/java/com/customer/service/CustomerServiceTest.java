package com.customer.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.customer.model.dto.ApiResponse;
import com.customer.model.dto.CustomerDTO;
import com.customer.model.entity.Customer;
import com.customer.repository.CustomerRepository;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

class CustomerServiceTest {

    @Test
    void saveReturnsSuccessResponseWhenRepositorySavesCustomer() {
        FakeCustomerRepository repository = new FakeCustomerRepository();
        CustomerService service = serviceWith(repository.asRepository());
        CustomerDTO request = customerDto(null, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1));
        Customer saved = customer(1L, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1));

        repository.savedCustomer = saved;

        ResponseEntity<ApiResponse> response = service.save(request);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(200);
        assertThat(response.getBody().getMessage()).isEqualTo("Customer saved successfully");
        assertThat(response.getBody().getData()).isEqualTo(saved);
        assertThat(repository.receivedCustomer).isEqualTo(request.toEntity());
    }

    @Test
    void saveReturnsServerErrorWhenRepositoryThrowsException() {
        FakeCustomerRepository repository = new FakeCustomerRepository();
        CustomerService service = serviceWith(repository.asRepository());
        CustomerDTO request = customerDto(null, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1));

        repository.saveException = new RuntimeException("database unavailable");

        ResponseEntity<ApiResponse> response = service.save(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Error saving customer: database unavailable");
        assertThat(response.getBody().getData()).isNull();
    }

    @Test
    void getAllCustomersReturnsSuccessResponseWhenRepositoryReturnsCustomers() {
        FakeCustomerRepository repository = new FakeCustomerRepository();
        CustomerService service = serviceWith(repository.asRepository());
        List<Customer> customers = List.of(
                customer(1L, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1)),
                customer(2L, "Asha", "Verma", LocalDate.of(1992, 5, 10)));

        repository.customers = customers;

        ResponseEntity<ApiResponse> response = service.getAllCustomers();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(200);
        assertThat(response.getBody().getMessage()).isEqualTo("Customers retrieved successfully");
        assertThat(response.getBody().getData()).isEqualTo(customers);
        assertThat(repository.findAllCalled).isTrue();
    }

    @Test
    void getAllCustomersReturnsServerErrorWhenRepositoryThrowsException() {
        FakeCustomerRepository repository = new FakeCustomerRepository();
        CustomerService service = serviceWith(repository.asRepository());
        repository.findAllException = new RuntimeException("query failed");

        ResponseEntity<ApiResponse> response = service.getAllCustomers();

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Error retrieving customers: query failed");
        assertThat(response.getBody().getData()).isNull();
    }

    private CustomerDTO customerDto(Long id, String firstName, String lastName, LocalDate dateOfBirth) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(id);
        customerDTO.setFirstName(firstName);
        customerDTO.setLastName(lastName);
        customerDTO.setDateOfBirth(dateOfBirth);
        return customerDTO;
    }

    private Customer customer(Long id, String firstName, String lastName, LocalDate dateOfBirth) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setDateOfBirth(dateOfBirth);
        return customer;
    }

    private CustomerService serviceWith(CustomerRepository repository) {
        CustomerService service = new CustomerService();
        ReflectionTestUtils.setField(service, "repository", repository);
        return service;
    }

    private static class FakeCustomerRepository {
        private Customer savedCustomer;
        private Customer receivedCustomer;
        private RuntimeException saveException;
        private RuntimeException findAllException;
        private List<Customer> customers = new ArrayList<>();
        private boolean findAllCalled;

        private CustomerRepository asRepository() {
            return (CustomerRepository) Proxy.newProxyInstance(
                    CustomerRepository.class.getClassLoader(),
                    new Class<?>[] {CustomerRepository.class},
                    (proxy, method, args) -> {
                        if ("save".equals(method.getName())) {
                            receivedCustomer = (Customer) args[0];
                            if (saveException != null) {
                                throw saveException;
                            }
                            return Objects.requireNonNull(savedCustomer);
                        }
                        if ("findAll".equals(method.getName())) {
                            findAllCalled = true;
                            if (findAllException != null) {
                                throw findAllException;
                            }
                            return customers;
                        }
                        if ("toString".equals(method.getName())) {
                            return "FakeCustomerRepository";
                        }
                        throw new UnsupportedOperationException(method.getName() + " is not supported in this test");
                    });
        }
    }
}
