package com.chefSearch.service;

import com.chefSearch.model.Chef;
import org.apache.jena.rdf.model.StmtIterator;

import java.util.List;

public interface RatingsService {
    void createRatings(Chef chefModel, StmtIterator ratingsIterator, List<String> ratings);
}