package com.chefSearch.service.implementation;

import com.chefSearch.model.Book;
import com.chefSearch.model.Chef;
import com.chefSearch.model.Cuisine;
import com.chefSearch.model.Restaurant;
import com.chefSearch.service.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChefServiceImpl implements ChefService {

    public static String URL = "http://dbpedia.org/data/XXX.ttl";
    public static String WIKI_URL = "http://en.wikipedia.org/wiki/XXX";
    private final BookService bookService;
    private final RatingsService ratingsService;
    private final TvShowService tvShowService;
    private final CuisineService cuisineService;
    private final RestaurantService restaurantService;

    public ChefServiceImpl(BookService bookService, RatingsService ratingsService, TvShowService tvShowService, CuisineService cuisineService, RestaurantService restaurantService) {
        this.bookService = bookService;
        this.ratingsService = ratingsService;
        this.tvShowService = tvShowService;
        this.cuisineService = cuisineService;
        this.restaurantService = restaurantService;
    }

    @Override
    public Chef getChef(String chef) throws IOException {

        List<String> ratings = new ArrayList<>();
        List<String> tvShows = new ArrayList<>();
        List<String> cuisines = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        List<Cuisine> cuisineList = new ArrayList<>();
        List<Restaurant> restaurantList = new ArrayList<>();
        Chef chefModel = new Chef();
        Book bookModel = new Book();
        Restaurant restaurantModel = new Restaurant();
        Cuisine cuisineModel = new Cuisine();

        chef = chef.replace(" ", "_");
        URL = URL.replace("XXX", chef);
        Model model = ModelFactory.createDefaultModel();

        RDFParser.source(URL).httpAccept("text/turtle").parse(model.getGraph());

        String resURL = URL.replace("data", "resource");
        URL = URL.replace(chef, "XXX");
        resURL = resURL.replace(".ttl", "");

        Resource chefResource = model.getResource(resURL);
        createChef(chefResource, chefModel, chef);

        /* BOOKS */
        Property bookProperty = model.getProperty("http://dbpedia.org/ontology/author");
        ResIterator bookIterator = model.listSubjectsWithProperty(bookProperty);
        while (bookIterator.hasNext()) {
            String bookUrl = bookIterator.nextResource().getProperty(bookProperty).getSubject().toString();

            Model modelBook = ModelFactory.createDefaultModel();
            RDFParser.source(bookUrl).httpAccept("text/turtle").parse(modelBook.getGraph());
            Resource bookResource = modelBook.getResource(bookUrl);

            bookService.createBooks(bookResource, bookModel, books);
        }
        chefModel.setBooks(books);
        books = new ArrayList<>();

        /* RATINGS */
        StmtIterator ratingsIterator = chefResource.listProperties(new PropertyImpl("http://dbpedia.org/property/ratings"));
        ratingsService.createRatings(chefModel, ratingsIterator, ratings);

        /* TV SHOWS */
        Property propertyStarring = model.getProperty("http://dbpedia.org/ontology/starring");
        ResIterator tvShowIterator = model.listSubjectsWithProperty(propertyStarring);
        tvShowService.createTvShows(chefModel, tvShowIterator, tvShows, propertyStarring);

        /* CUISINES */
        StmtIterator cuisineIterator = chefResource.listProperties(new PropertyImpl("http://dbpedia.org/property/style"));
        while (cuisineIterator.hasNext()) {
            Statement statement = cuisineIterator.nextStatement();
            String objectUrl = statement.getObject().toString();

            Model modelRestaurant = ModelFactory.createDefaultModel();
            RDFParser.source(objectUrl).httpAccept("text/turtle").parse(modelRestaurant.getGraph());
            Resource cuisineResource = modelRestaurant.getResource(objectUrl);

            cuisineService.createCuisines(cuisineResource, cuisineModel, cuisineList, modelRestaurant, cuisines);
            cuisines = new ArrayList<>();
            cuisineModel = new Cuisine();
        }
        chefModel.setCuisines(cuisineList);

        /* RESTAURANTS */
        Property propertyOwner = model.getProperty("http://dbpedia.org/ontology/owner");
        ResIterator propertyOwnerIterator = model.listSubjectsWithProperty(propertyOwner);
        while (propertyOwnerIterator.hasNext()) {
            Resource restaurantResourceSubject = propertyOwnerIterator.nextResource().getProperty(propertyOwner).getSubject();

            String resUrl = restaurantResourceSubject.toString();
            Model modelRestaurant = ModelFactory.createDefaultModel();
            RDFParser.source(resUrl).httpAccept("text/turtle").parse(modelRestaurant.getGraph());

            Resource restaurantResource = modelRestaurant.getResource(resUrl);

            restaurantService.createRestaurants(restaurantResource, restaurantModel, restaurantList);
            restaurantModel = new Restaurant();
        }
        if (restaurantList.isEmpty()) {
            chefModel.setOwnerOf(Collections.EMPTY_LIST);
        } else {
            chefModel.setOwnerOf(restaurantList);
        }
        return chefModel;
    }

    private void createChef(Resource chefResource, Chef chefModel, String chef) throws IOException {
        String chefName = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/property/name"), "en").getObject().toString().replace("@en", "");
        String birthPlace = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/birthPlace")).getResource().getLocalName().replace("_", "");
        String thumbnail = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/thumbnail")).getObject().toString();
        String chefWebsite = WIKI_URL.replace("XXX", chefName.replace(" ", "_")).replace("@en", "");
        SPARQL(chef, chefModel);
        chefModel.setName(chefName);
        chefModel.setBirthPlace(birthPlace);
        chefModel.setPhoto(thumbnail);
        chefModel.setWebsite(chefWebsite);
    }

    public static void SPARQL(String chef, Chef chefModel) throws IOException {
        String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dbp: <http://dbpedia.org/property/>\n" +
                "PREFIX dbr: <http://dbpedia.org/resource/>" +
                "select  ?bio ?birthDate\n" +
                "where{\n" +
                "dbr:CHEF dbo:abstract ?bio\n;" +
                "dbo:birthDate ?birthDate." +
                "FILTER(lang(?bio) = \"en\")\n" +
                "}";
        queryString = queryString.replace("CHEF", chef);
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
        ResultSet resultSet = queryExecution.execSelect();
        while (resultSet.hasNext()) {
            QuerySolution querySolution = resultSet.nextSolution();
            String[] parts = querySolution.toString().split("\"");
            String bio = parts[1];
            String birthDate = parts[3];
            chefModel.setBio(bio);
            chefModel.setBirthDate(birthDate);
        }
    }
}