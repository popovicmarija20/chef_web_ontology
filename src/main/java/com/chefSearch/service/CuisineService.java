package com.chefSearch.service;

import com.chefSearch.model.Cuisine;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public interface CuisineService {
    void createCuisines(Resource cuisineResource, Cuisine cuisineModel, List<Cuisine> cuisineList, Model modelRestaurant, List<String> cousines);
}