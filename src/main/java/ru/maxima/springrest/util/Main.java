package ru.maxima.springrest.util;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;
import ru.maxima.springrest.test_model.Person;

public class Main {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://reqres.in/api/users";

        Person person = Person.builder()
                .name("Trinity12345")
                .job("Wife of Messiah Neo")
                .build();

        HttpEntity<Person> request = new HttpEntity<>(person);


        Person response = restTemplate.postForObject(url, request, Person.class);


        System.out.println(response);

    }
}









//        RestEntity response = restTemplate.getForObject(url, RestEntity.class);
//
//        System.out.println(response);

//        Map<String, String> json = new HashMap<>();
//
//        json.put("name", "Trinity123");
//        json.put("job", "Wife of Messiah");
//
//        HttpEntity<Map<String, String>> request = new HttpEntity<>(json);
