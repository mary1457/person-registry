package it.bars.person_registry.services;

import it.bars.person_registry.entities.Address;
import it.bars.person_registry.entities.Person;
import it.bars.person_registry.exceptions.BadRequestException;
import it.bars.person_registry.exceptions.NotFoundException;
import it.bars.person_registry.payloads.PersonDTO;
import it.bars.person_registry.repositories.AddressRepository;
import it.bars.person_registry.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Person savePerson(PersonDTO personDTO) {
        // Check if the person already exists by tax code
        this.personRepository.findByTaxCode(personDTO.taxCode())
                .ifPresent(person -> {
                    throw new BadRequestException("Person with tax code " + personDTO.taxCode() + " already exists!");
                });

        Address address = personDTO.address();

        // Check if the address already exists, otherwise save it
        Address addressToUse = this.addressRepository.findExistingAddress(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getProvince(),
                address.getCountry()
        ).orElseGet(() -> this.addressRepository.save(address));

        // Create new person
        Person person = new Person();
        person.setTaxCode(personDTO.taxCode());
        person.setName(personDTO.name());
        person.setSurname(personDTO.surname());
        person.setAddress(addressToUse);

        return this.personRepository.save(person);
    }

    public Page<Person> findAll(int page, int size, String sortBy) {
        if (size > 100) size = 100;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.personRepository.findAll(pageable);
    }

    public Person findByTaxCode(String taxCode) {
        return this.personRepository.findByTaxCode(taxCode)
                .orElseThrow(() -> new NotFoundException("Person with tax code " + taxCode + " not found!"));
    }

    public Person updatePerson(String taxCode, PersonDTO personDTO) {
        Person person = this.findByTaxCode(taxCode);

        Address address = personDTO.address();
        Address addressToUse = this.addressRepository.findExistingAddress(
                address.getStreet(),
                address.getNumber(),
                address.getCity(),
                address.getProvince(),
                address.getCountry()
        ).orElseGet(() -> this.addressRepository.save(address));

        person.setName(personDTO.name());
        person.setSurname(personDTO.surname());
        person.setAddress(addressToUse);

        return this.personRepository.save(person);
    }

    public void deletePerson(String taxCode) {
        Person person = this.findByTaxCode(taxCode);
        this.personRepository.delete(person);
    }

    public List<Person> search(String surname, String province) {
        return this.personRepository.searchByFilters(surname, province);
    }
}
