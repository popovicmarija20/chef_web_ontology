import model.Book;
import model.Chef;
import model.Cousine;
import model.Restaurant;
import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.sparql.vocabulary.FOAF;

import java.io.IOException;
import java.util.*;

public class Main {

    public static String URL = "http://dbpedia.org/data/XXX.ttl";
    public static String WIKI_URL = "http://en.wikipedia.org/wiki/XXX";


    public static void main(String[] args) throws IOException {
        List<String> ratings = new ArrayList<>();
        List<String> tvShows = new ArrayList<>();
        List<String> cousines = new ArrayList<>();
        List<Book> books = new ArrayList<>();
        List<Cousine> cousineList = new ArrayList<>();
        List<Restaurant> restaurantList = new ArrayList<>();
        Chef chefModel = new Chef();
        Book bookModel = new Book();
        Restaurant restaurantModel = new Restaurant();
        Cousine cousineModel = new Cousine();

        Scanner scanner = new Scanner(System.in);
        String chef = scanner.nextLine();
        chef = chef.replace(" ", "_");
        URL = URL.replace("XXX", chef);
        Model model = ModelFactory.createDefaultModel();

        RDFParser.source(URL).httpAccept("text/turtle").parse(model.getGraph());

        String resURL = URL.replace("data", "resource");
        URL = URL.replace(chef, "XXX");
        resURL = resURL.replace(".ttl", "");

        Resource chefResource = model.getResource(resURL);

        String chefName1 = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/property/name"), "en").getObject().toString();
        String bio = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();
        String chefWebsite = WIKI_URL.replace("XXX", chefName1.replace(" ", "_")).replace("@en", "");
        chefModel.setBio(bio);
        chefModel.setName(chefName1);
        chefModel.setWebsite(chefWebsite);

        /* BOOKS */
        Property bookProperty = model.getProperty("http://dbpedia.org/ontology/author");
        ResIterator bookIterator = model.listSubjectsWithProperty(bookProperty);
        while (bookIterator.hasNext()) {
            String bookUrl = bookIterator.nextResource().getProperty(bookProperty).getSubject().toString();

            Model modelBook = ModelFactory.createDefaultModel();
            RDFParser.source(bookUrl).httpAccept("text/turtle").parse(modelBook.getGraph());
            Resource bookResource = modelBook.getResource(bookUrl);

            String bookName = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/property/name")).getObject().toString().replace("@en", "");
            String bookDescription = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();
            String published = "";
            String bookWebSite = "";
            if (bookResource.hasProperty(new PropertyImpl("http://dbpedia.org/property/published"))) {
                published = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/property/published")).getObject().toString();
            }
            if (bookResource.hasProperty(new PropertyImpl("http://dbpedia.org/ontology/releaseDate"))) {
                published = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/releaseDate")).getObject().toString();
            }
            String bookWebsite = null;
            if (bookResource.hasProperty(new PropertyImpl("http://dbpedia.org/property/website"))) {
                bookWebsite = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/property/website")).getObject().toString();
            }

            published = published.replace("^^http://www.w3.org/2001/XMLSchema#date", "");
            bookModel.setName(bookName);
            bookModel.setDescription(bookDescription);
            bookModel.setPublished(published);
            bookModel.setWebsite(Optional.ofNullable(bookWebsite));
            books.add(bookModel);
        }
        chefModel.setBooks(books);
        //TODO maybe reference to empty array when you switch authors?
        books = new ArrayList<>();

        /* RATINGS */
        StmtIterator ratingsIterator = chefResource.listProperties(new PropertyImpl("http://dbpedia.org/property/ratings"));
        if (ratingsIterator.hasNext()) {
            while (ratingsIterator.hasNext()) {
                Statement rating = ratingsIterator.nextStatement();

                String ratingObject = rating.getObject().toString();
                ratings.add(ratingObject.replace("@en", ""));
            }
        }
        chefModel.setRatings(ratings);

        /* TV SHOWS */
        Property propertyStarring = model.getProperty("http://dbpedia.org/ontology/starring");
        ResIterator tvShowIterator = model.listSubjectsWithProperty(propertyStarring);
        while (tvShowIterator.hasNext()) {
            String tvShowWithUnderScore = tvShowIterator.nextResource().getProperty(propertyStarring).getSubject().getLocalName();
            String tvShow = tvShowWithUnderScore.replace("_", " ");
            tvShow = tvShow.replaceFirst("s", "");
            tvShows.add(tvShow);
        }
        chefModel.setTvShows(tvShows);

        /* CUSINES */
        StmtIterator cusineIterator = chefResource.listProperties(new PropertyImpl("http://dbpedia.org/property/style"));
        while (cusineIterator.hasNext()) {
            Statement statement = cusineIterator.nextStatement();
            String objectUrl = statement.getObject().toString();

            Model modelRestaurant = ModelFactory.createDefaultModel();
            RDFParser.source(objectUrl).httpAccept("text/turtle").parse(modelRestaurant.getGraph());
            Resource cuisineResource = modelRestaurant.getResource(objectUrl);

            String name = cuisineResource.getLocalName().replace("_", " ");
            String description = cuisineResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();
            System.out.println(description);
            System.out.println(name);
            cousineModel.setName(name);
            cousineModel.setDescription(description);

            //fetch chefs
            Property propertyChefNames = modelRestaurant.getProperty("http://dbpedia.org/property/style");
            ResIterator propertyChefNamesIterator = modelRestaurant.listSubjectsWithProperty(propertyChefNames);
            // TODO: implement SPARQL query for filtering only 5 chefs
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
        chefModel.setCuisines(cousineList);

        /* RESTAURANTS */
        Property propertyOwner = model.getProperty("http://dbpedia.org/ontology/owner");
        ResIterator propertyOwnerIterator = model.listSubjectsWithProperty(propertyOwner);
        while (propertyOwnerIterator.hasNext()) {
            Resource restaurantResourceSubject = propertyOwnerIterator.nextResource().getProperty(propertyOwner).getSubject();
            System.out.println(restaurantResourceSubject.toString());

            String resUrl = restaurantResourceSubject.toString();
            Model modelRestaurant = ModelFactory.createDefaultModel();
            RDFParser.source(resUrl).httpAccept("text/turtle").parse(modelRestaurant.getGraph());

            Resource restaurantResource = modelRestaurant.getResource(resUrl);
            String restaurantName = restaurantResource.getProperty(FOAF.name, "en").getObject().toString().replace("@en", "");

            String description = restaurantResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();

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
            restaurantModel = new Restaurant();
        }
        if (restaurantList.isEmpty()) {
            chefModel.setOwnerOf(Collections.EMPTY_LIST);
        } else {
            chefModel.setOwnerOf(restaurantList);
        }
    }
}
