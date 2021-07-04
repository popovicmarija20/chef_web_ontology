package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Chef {

    String name;
    String bio;
    String website;
    List<String> ratings;
    List<String> tvShows;
    List<Restaurant> ownerOf;
    List<Cousine> cuisines;
    List<Book> books;

}
