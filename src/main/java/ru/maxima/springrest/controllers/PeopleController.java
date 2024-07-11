package ru.maxima.springrest.controllers;

// CRUD

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.maxima.springrest.dto.PersonDTO;
import ru.maxima.springrest.exceptions.IdMoreThanTenThousandsException;
import ru.maxima.springrest.exceptions.PersonErrorResponse;
import ru.maxima.springrest.exceptions.PersonNotCreatedException;
import ru.maxima.springrest.exceptions.PersonNotFoundException;
import ru.maxima.springrest.models.Person;
import ru.maxima.springrest.repositories.PeopleRepository;
import ru.maxima.springrest.service.PeopleService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final ModelMapper modelMapper;
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleController(PeopleService peopleService,
                            ModelMapper modelMapper,
                            PeopleRepository peopleRepository) {
        this.peopleService = peopleService;
        this.modelMapper = modelMapper;
        this.peopleRepository = peopleRepository;
    }

    @GetMapping()
    public List<PersonDTO> getAllPeople() {
        return peopleService.getAllPeople(false).stream()
                .map(p -> modelMapper.map(p, PersonDTO.class))
                .toList();
    }

    @DeleteMapping("/{id}")
    public void deletePersonById(@PathVariable("id") Long id) {
        peopleService.delete(id);
    }

    @GetMapping("/deleted")
    public List<PersonDTO> getAllRemovedPeople() {
        return peopleService.getAllPeople(true).stream()
                .map(p -> modelMapper.map(p, PersonDTO.class))
                .toList();
    }

    @GetMapping("/{id}")
    public PersonDTO getPersonById(@PathVariable("id") Long id) {
        Person byId = peopleService.findById(id);
        return peopleService.convertFromPersonToPersonDTO(byId);
    }

    @PutMapping("/{id}")
    public void updatePersonById(@PathVariable("id") Long id,
                                 @RequestBody @Valid PersonDTO personDTO,
                                 BindingResult bindingResult) {
        checkForErrors(bindingResult);
        peopleService.update(personDTO, id);
    }

    private void checkForErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError :
                    fieldErrors) {
                stringBuilder.append(fieldError.getField())
                        .append(" - ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(stringBuilder.toString());
        }
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> savePerson(@RequestBody @Valid PersonDTO personDTO,
                                                 BindingResult bindingResult) {
        checkForErrors(bindingResult);
        Person person = peopleService.convertFromDTOToPerson(personDTO);
        peopleService.save(person);
        return ResponseEntity.ok(HttpStatus.OK);
    }


    // ExceptionHandler

    @ExceptionHandler({IdMoreThanTenThousandsException.class})
    public ResponseEntity<PersonErrorResponse> handleId10000Exception(IdMoreThanTenThousandsException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({PersonNotFoundException.class})
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PersonNotCreatedException.class})
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
