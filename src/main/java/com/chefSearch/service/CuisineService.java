package com.chefSearch.service;

import com.chefSearch.model.Cousine;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public interface CuisineService {
    void createCuisines(Resource cuisineResource, Cousine cousineModel, List<Cousine> cousineList, Model modelRestaurant, List<String> cousines);
}