package it.bars.person_registry.controllers;

import it.bars.person_registry.entities.Person;
import it.bars.person_registry.exceptions.BadRequestException;
import it.bars.person_registry.payloads.PersonDTO;
import it.bars.person_registry.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Person createPerson(@RequestBody @Validated PersonDTO personDTO, BindingResult validationResult) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("There are errors in the payload! " + message);
        }
        return this.personService.savePerson(personDTO);
    }

    @GetMapping
    public Page<Person> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "taxCode") String sortBy
    ) {
        return this.personService.findAll(page, size, sortBy);
    }

    @GetMapping("/{taxCode}")
    public Person getPersonByTaxCode(@PathVariable String taxCode) {
        return this.personService.findByTaxCode(taxCode);
    }

    @PutMapping("/{taxCode}")
    public Person updatePerson(
            @PathVariable String taxCode,
            @RequestBody @Validated PersonDTO personDTO,
            BindingResult validationResult
    ) {
        if (validationResult.hasErrors()) {
            String message = validationResult.getAllErrors()
                    .stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.joining(". "));
            throw new BadRequestException("There are errors in the payload! " + message);
        }
        return this.personService.updatePerson(taxCode, personDTO);
    }

    @DeleteMapping("/{taxCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable String taxCode) {
        this.personService.deletePerson(taxCode);
    }

    @GetMapping("/search")
    public List<Person> searchPersons(
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String province
    ) {
        return this.personService.search(surname, province);
    }
}
