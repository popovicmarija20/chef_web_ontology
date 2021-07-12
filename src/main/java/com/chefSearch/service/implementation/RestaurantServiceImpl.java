package com.chefSearch.service.implementation;

import com.chefSearch.model.Restaurant;
import com.chefSearch.service.RestaurantService;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {
    @Override
    public void createRestaurants(Resource restaurantResource, Restaurant restaurantModel, List<Restaurant> restaurantList) {
        String restaurantName = restaurantResource.getProperty(FOAF.name, "en").getObject().toString().replace("@en", "");

        String description = restaurantResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString().replace("@en", "");

        Optional<String> country = Optional.ofNullable(restaurantResource.getProperty(new PropertyImpl("http://dbpedia.org/property/country")).getObject().toString());
        if (country.isPresent()) {
            String countryName = "";
            if (country.get().contains("resource")) {
                String[] parts = country.get().split("/");
                countryName = parts[parts.length - 1].replace("_", " ");
            } else {
                countryName = country.get().toString().replace("@en", "");
            }
            restaurantModel.setCountry(countryName);

        }
        String website = "";
        if (restaurantResource.hasProperty(new PropertyImpl("http://dbpedia.org/property/website"))) {
            website = restaurantResource.getProperty(new PropertyImpl("http://dbpedia.org/property/website")).getObject().toString();
            restaurantModel.setWebsite(website);
        }
        restaurantModel.setName(restaurantName);
        restaurantModel.setDescription(description);
        restaurantList.add(restaurantModel);
    }
}