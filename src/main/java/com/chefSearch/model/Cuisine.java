package com.chefSearch.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Cuisine {

    String name;
    List<String> chefNames;
    String description;

}
