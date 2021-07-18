package com.chefSearch.service.implementation;

import com.chefSearch.model.Cuisine;
import com.chefSearch.service.CuisineService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CuisineServiceImpl implements CuisineService {
    @Override
    public void createCuisines(Resource cuisineResource, Cuisine cuisineModel, List<Cuisine> cuisineList, Model modelRestaurant, List<String> cousines) {
        String name = cuisineResource.getLocalName().replace("_", " ");
        String description = cuisineResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString().replace("@en", "");
        cuisineModel.setName(name);
        cuisineModel.setDescription(description);

        //fetch chefs
        Property propertyChefNames = modelRestaurant.getProperty("http://dbpedia.org/property/style");
        ResIterator propertyChefNamesIterator = modelRestaurant.listSubjectsWithProperty(propertyChefNames);
        int counter = 0;
        while (propertyChefNamesIterator.hasNext()) {
            counter++;
            if (counter == 5) {
                cuisineModel.setChefNames(cousines);
                break;
            } else {
                Resource chefNameResourceSubject = propertyChefNamesIterator.nextResource().getProperty(propertyChefNames).getSubject();
                String[] parts = chefNameResourceSubject.toString().split("/");
                String chefName = parts[parts.length - 1].replace("_", " ");
                cousines.add(chefName);
            }
        }
        cuisineList.add(cuisineModel);
    }
}
