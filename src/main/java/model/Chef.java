package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Chef {

    String name;
    String bio;
    String website;
    List<String> ratings;
    List<String> tvShows;
    Restaurant restaurant;
    List<Restaurant> ownerOf;
    List<Cousine> cuisines;
    List<Book> books;

}
