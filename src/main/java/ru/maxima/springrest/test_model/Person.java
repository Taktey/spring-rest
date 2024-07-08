package ru.maxima.springrest.test_model;

import lombok.*;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private String name;
    private String job;
    private String id;
    private String createdAt;


}
