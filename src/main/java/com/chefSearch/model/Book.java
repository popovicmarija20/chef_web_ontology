package com.chefSearch.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class Book {

    String name;
    String description;
    Optional<String> website;
    String published;


}
