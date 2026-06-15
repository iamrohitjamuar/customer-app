package com.customer.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.customer.model.entity.Customer;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository repository;

    @Test
    void saveAndFindAllPersistsCustomerEntity() {
        Customer customer = new Customer();
        customer.setFirstName("Rohit");
        customer.setLastName("Jamuar");
        customer.setDateOfBirth(LocalDate.of(1995, 1, 1));

        Customer saved = repository.save(customer);

        List<Customer> customers = repository.findAll();

        assertThat(saved.getId()).isNotNull();
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getFirstName()).isEqualTo("Rohit");
        assertThat(customers.get(0).getLastName()).isEqualTo("Jamuar");
        assertThat(customers.get(0).getDateOfBirth()).isEqualTo(LocalDate.of(1995, 1, 1));
    }
}
