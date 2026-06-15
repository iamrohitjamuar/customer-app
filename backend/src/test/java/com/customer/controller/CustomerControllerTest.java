package com.customer.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.customer.config.GlobalExceptionHandler;
import com.customer.model.dto.ApiResponse;
import com.customer.model.dto.CustomerDTO;
import com.customer.model.entity.Customer;
import com.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CustomerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private FakeCustomerService customerService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        customerService = new FakeCustomerService();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        CustomerController controller = new CustomerController();
        ReflectionTestUtils.setField(controller, "service", customerService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    void createCustomerReturnsCreatedCustomer() throws Exception {
        Customer savedCustomer = customer(1L, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1));
        customerService.saveResponse = ResponseEntity.ok(new ApiResponse(200, "Customer saved successfully", savedCustomer));

        CustomerDTO request = customerDto(null, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Customer saved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.firstName").value("Rohit"))
                .andExpect(jsonPath("$.data.lastName").value("Jamuar"))
                .andExpect(jsonPath("$.data.dateOfBirth").value("1995-01-01"));

        assertThat(customerService.savedCustomer).isNotNull();
        assertThat(customerService.savedCustomer.getFirstName()).isEqualTo("Rohit");
        assertThat(customerService.savedCustomer.getLastName()).isEqualTo("Jamuar");
    }

    @Test
    void getCustomersReturnsAllCustomers() throws Exception {
        List<Customer> customers = List.of(
                customer(1L, "Rohit", "Jamuar", LocalDate.of(1995, 1, 1)),
                customer(2L, "Asha", "Verma", LocalDate.of(1992, 5, 10)));

        customerService.getAllResponse =
                ResponseEntity.ok(new ApiResponse(200, "Customers retrieved successfully", customers));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].firstName").value("Rohit"))
                .andExpect(jsonPath("$.data[1].lastName").value("Verma"));

        assertThat(customerService.getAllCalled).isTrue();
    }

    @Test
    void createCustomerReturnsValidationErrorsForInvalidPayload() throws Exception {
        CustomerDTO request = customerDto(null, "", "", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.data.firstName").value("First name is required"))
                .andExpect(jsonPath("$.data.lastName").value("Last name is required"))
                .andExpect(jsonPath("$.data.dateOfBirth").value("Date of birth must be in the past"));
    }

    @Test
    void createCustomerReturnsValidationErrorWhenDateOfBirthIsMissing() throws Exception {
        CustomerDTO request = customerDto(null, "Rohit", "Jamuar", null);

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.data.dateOfBirth").value("Date of birth is required"));
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

    private static class FakeCustomerService extends CustomerService {
        private CustomerDTO savedCustomer;
        private boolean getAllCalled;
        private ResponseEntity<ApiResponse> saveResponse;
        private ResponseEntity<ApiResponse> getAllResponse;

        FakeCustomerService() {
        }

        @Override
        public ResponseEntity<ApiResponse> save(CustomerDTO customerDTO) {
            savedCustomer = customerDTO;
            return saveResponse;
        }

        @Override
        public ResponseEntity<ApiResponse> getAllCustomers() {
            getAllCalled = true;
            return getAllResponse;
        }
    }
}
