package it.bars.person_registry.payloads;

import it.bars.person_registry.entities.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PersonDTO(

        @NotBlank(message = "{person.taxCode.required}")
        @Pattern(
                regexp = "^[A-Z]{6}[0-9]{2}[A-Z][0-9]{2}[A-Z][0-9]{3}[A-Z]$",
                message = "{person.taxCode.invalidFormat}"
        )
        String taxCode,

        @NotBlank(message = "{person.name.required}")
        @Size(min = 2, max = 40, message = "{person.name.size}")
        String name,

        @NotBlank(message = "{person.surname.required}")
        @Size(min = 2, max = 40, message = "{person.surname.size}")
        String surname,

        @NotNull(message = "{person.address.required}")
        @Valid
        Address address
) {
}
