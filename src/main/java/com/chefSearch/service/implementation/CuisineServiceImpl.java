package com.chefSearch.service.implementation;

import com.chefSearch.model.Cousine;
import com.chefSearch.service.CuisineService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CuisineServiceImpl implements CuisineService {
    @Override
    public void createCuisines(Resource cuisineResource, Cousine cousineModel, List<Cousine> cousineList, Model modelRestaurant, List<String> cousines) {
        String name = cuisineResource.getLocalName().replace("_", " ");
        String description = cuisineResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString().replace("@en", "");
        cousineModel.setName(name);
        cousineModel.setDescription(description);

        //fetch chefs
        Property propertyChefNames = modelRestaurant.getProperty("http://dbpedia.org/property/style");
        ResIterator propertyChefNamesIterator = modelRestaurant.listSubjectsWithProperty(propertyChefNames);
        int counter = 0;
        while (propertyChefNamesIterator.hasNext()) {
            counter++;
            if (counter == 5) {
                cousineModel.setChefNames(cousines);
                cousines = new ArrayList<>();
                break;
            } else {
                Resource chefNameResourceSubject = propertyChefNamesIterator.nextResource().getProperty(propertyChefNames).getSubject();
                String[] parts = chefNameResourceSubject.toString().split("/");
                String chefName = parts[parts.length - 1].replace("_", " ");
                cousines.add(chefName);
            }
        }
//            cousineModel.setChefNames(cousines);
        cousineList.add(cousineModel);
        cousineModel = new Cousine();
    }
}
