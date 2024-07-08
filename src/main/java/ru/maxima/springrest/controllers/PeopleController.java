package ru.maxima.springrest.controllers;

// CRUD

import jakarta.validation.Valid;
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
import ru.maxima.springrest.service.PeopleService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping()
    public List<PersonDTO> getAllPeople() {
        List<Person> allPeople = peopleService.getAllPeople();
        List<PersonDTO> personDTOList = new ArrayList<>();
        for (Person p: allPeople) {
            personDTOList.add(peopleService.convertFromPersonToPersonDTO(p));
        }

        return personDTOList; // Jackson сконвертирует объекты в Json
    }

    @GetMapping("/{id}") // 1666666
    public PersonDTO getPersonById(@PathVariable("id") Long id) {
        Person byId = peopleService.findById(id);
        return peopleService.convertFromPersonToPersonDTO(byId); // Jackson сконвертирует объекты в Json
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> savePerson(@RequestBody @Valid PersonDTO personDTO,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder stringBuilder = new StringBuilder();

            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError:
                 fieldErrors) {
                stringBuilder.append(fieldError.getField())
                        .append(" - ")
                        .append(fieldError.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(stringBuilder.toString());
        }

        Person person = peopleService.convertFromDTOToPerson(personDTO);

        peopleService.save(person);

        return ResponseEntity.ok(HttpStatus.OK);
    }




    // ExceptionHandler

    @ExceptionHandler({ IdMoreThanTenThousandsException.class})
    public ResponseEntity<PersonErrorResponse> handleId10000Exception(IdMoreThanTenThousandsException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({ PersonNotFoundException.class})
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ PersonNotCreatedException.class})
    public ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException ex) {
        PersonErrorResponse response = new PersonErrorResponse(
                ex.getMessage(), new Date()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
