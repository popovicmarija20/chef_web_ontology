package com.chefSearch.service;

import com.chefSearch.model.Chef;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;

import java.util.List;

public interface TvShowService {
    void createTvShows(Chef chefModel, ResIterator tvShowIterator, List<String> tvShows, Property propertyStarring);
}