package ru.maxima.springrest.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class PersonErrorResponse {
    private String message;
    private Date date;
}
