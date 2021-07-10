package com.chefSearch.service.implementation;

import com.chefSearch.model.Chef;
import com.chefSearch.service.RatingsService;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingsServiceImpl implements RatingsService {
    @Override
    public void createRatings(Chef chefModel, StmtIterator ratingsIterator, List<String> ratings) {
        if (ratingsIterator.hasNext()) {
            while (ratingsIterator.hasNext()) {
                Statement rating = ratingsIterator.nextStatement();

                String ratingObject = rating.getObject().toString();
                ratings.add(ratingObject.replace("@en", ""));
            }
        }
        chefModel.setRatings(ratings);
    }
}
