package com.customer.model.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.customer.model.entity.Customer;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class CustomerDTOTest {

    @Test
    void toEntityCopiesAllCustomerFieldsIncludingId() {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(7L);
        dto.setFirstName("Rohit");
        dto.setLastName("Jamuar");
        dto.setDateOfBirth(LocalDate.of(1995, 1, 1));

        Customer entity = dto.toEntity();

        assertThat(entity.getId()).isEqualTo(7L);
        assertThat(entity.getFirstName()).isEqualTo("Rohit");
        assertThat(entity.getLastName()).isEqualTo("Jamuar");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(1995, 1, 1));
    }

    @Test
    void toEntityLeavesIdNullWhenDtoHasNoId() {
        CustomerDTO dto = new CustomerDTO();
        dto.setFirstName("Asha");
        dto.setLastName("Verma");
        dto.setDateOfBirth(LocalDate.of(1992, 5, 10));

        Customer entity = dto.toEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getFirstName()).isEqualTo("Asha");
        assertThat(entity.getLastName()).isEqualTo("Verma");
        assertThat(entity.getDateOfBirth()).isEqualTo(LocalDate.of(1992, 5, 10));
    }

    @Test
    void apiResponseGettersAndSettersExposeResponseFields() {
        ApiResponse response = new ApiResponse(200, "ok", "initial");

        response.setCode(201);
        response.setMessage("created");
        response.setData("updated");

        assertThat(response.getCode()).isEqualTo(201);
        assertThat(response.getMessage()).isEqualTo("created");
        assertThat(response.getData()).isEqualTo("updated");
    }
}
