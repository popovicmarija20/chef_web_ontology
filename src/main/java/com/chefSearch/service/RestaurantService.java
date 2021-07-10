package com.chefSearch.service;

import com.chefSearch.model.Restaurant;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public interface RestaurantService {
    void createRestaurants(Resource restaurantResource, Restaurant restaurantModel, List<Restaurant> restaurantList);
}