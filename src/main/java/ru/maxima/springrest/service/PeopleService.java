package ru.maxima.springrest.service;

import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.maxima.springrest.dto.PersonDTO;
import ru.maxima.springrest.exceptions.IdMoreThanTenThousandsException;
import ru.maxima.springrest.exceptions.PersonNotFoundException;
import ru.maxima.springrest.models.Person;
import ru.maxima.springrest.repositories.PeopleRepository;
import ru.maxima.springrest.util.JWTUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {

    private final PeopleRepository repository;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;

    @Autowired
    public PeopleService(PeopleRepository repository, ModelMapper modelMapper, JWTUtil jwtUtil) {
        this.repository = repository;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public List<Person> getAllPeople(Boolean isRemoved) {

        String token = jwtUtil.generateToken("Viktor");

        String username = jwtUtil.validateTokenAndReturnUsername(token);

        System.out.println(username);

        List<Person> people = repository.findAllByRemoved(isRemoved);


        test();

        return people;
    }

    @Transactional
    public Person findById(Long id) {
        if (id > 10000) {
            throw new IdMoreThanTenThousandsException("Id more than 10000. Person with this id is not exist");
        }

        Optional<Person> byId = repository.findById(id);

        return byId.orElseThrow(() -> new PersonNotFoundException("Person is not found from DB Maxima"));
    }

    @Transactional
    public void save(Person person) {
        repository.save(person);
    }

    @Transactional
    public void update(PersonDTO personDTO, Long id) {
        Person toBeUpdated = findById(id);
        Person updates = convertFromDTOToPerson(personDTO);
        toBeUpdated.setName(updates.getName());
        toBeUpdated.setAge(updates.getAge());
        toBeUpdated.setEmail(updates.getEmail());
        repository.save(toBeUpdated);
    }

    @Transactional
    public void delete(Long id) {
        Person byId= findById(id);
        byId.setRemoved(true);
        repository.save(byId);
    }

    @Transactional
    public void test() {

        List<Person> v = repository.findPeopleByNameLike("V%");

        System.out.println(v);
    }

    public PersonDTO convertFromPersonToPersonDTO(Person person) {
        return modelMapper.map(person, PersonDTO.class);
    }

    public Person convertFromDTOToPerson(PersonDTO personDTO) {
        Person person = modelMapper.map(personDTO, Person.class);
        enrich(person);
        return person;
    }

    private void enrich(Person p) {
        p.setCreatedAt(LocalDateTime.now());
        p.setCreatedBy("ADMIN");
        p.setRole("ROLE_USER");
        p.setRemoved(false);
    }

}
