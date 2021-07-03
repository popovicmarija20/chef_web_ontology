import org.apache.jena.rdf.model.*;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.RDFParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static String URL = "http://dbpedia.org/data/XXX.ttl";
//    public static List<String> activeMembersToSend = new ArrayList<String>();
//    public static List<String> pastMembersToSend = new ArrayList<String>();
//    public static List<String> genresToSend = new ArrayList<String>();
//    public static List<String> recordLabelsToSend = new ArrayList<String>();

    public static void main(String[] args) throws IOException {
        List<String> ratings = new ArrayList<>(); //ratings, bio,
        List<String> tvShows = new ArrayList<>();
        List<String> cousines = new ArrayList<>();

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


        String bio = chefResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();

        /* RATINGS */
        StmtIterator ratingsIterator = chefResource.listProperties(new PropertyImpl("http://dbpedia.org/property/ratings"));
        if (ratingsIterator.hasNext()) {
            while (ratingsIterator.hasNext()) {
                Statement rating = ratingsIterator.nextStatement();

                String ratingObject = rating.getObject().toString();
                ratings.add(ratingObject.replace("@en", ""));
            }
        }
        /* TV SHOWS */
        Property propertyStarring = model.getProperty("http://dbpedia.org/ontology/starring");
        ResIterator tvShowIterator = model.listSubjectsWithProperty(propertyStarring);
        while (tvShowIterator.hasNext()) {
            String tvShowWithUnderScore = tvShowIterator.nextResource().getProperty(propertyStarring).getSubject().getLocalName();
            String tvShow = tvShowWithUnderScore.replace("_", " ");
            tvShow = tvShow.replaceFirst("s", "");
            tvShows.add(tvShow);
        }

        /* CUSINES */
        Property propertyCuisine = model.getProperty("http://dbpedia.org/property/style");
        ResIterator cusineIterator = model.listSubjectsWithProperty(propertyCuisine);
        while (cusineIterator.hasNext()) {
            Resource resource = cusineIterator.nextResource().getProperty(propertyCuisine).getResource();
            String name = resource.getLocalName();

            URL = URL.replace("XXX", name);
            Model modelCuisine = ModelFactory.createDefaultModel();
            RDFParser.source(URL).httpAccept("text/turtle").parse(modelCuisine.getGraph());
            String resUrl = URL.replace("data", "resource");
            URL = URL.replace(name, "XXX");
            resUrl = resUrl.replace(".ttl", "");

            Resource cuisineResource = modelCuisine.getResource(resUrl);

            String description = cuisineResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();
            System.out.println(name);
            System.out.println(description);
        }


//    public static void main(String[] args) throws IOException {
//        Scanner scanner = new Scanner(System.in);
//        String artist = scanner.nextLine();
//        artist = artist.replace(" ", "_");
//        URL = URL.replace("XXX", artist);
//        Model model = ModelFactory.createDefaultModel();
//
//        RDFParser.source(URL).httpAccept("text/turtle").parse(model.getGraph());
//
////        try {
////            model.write(new FileWriter("C:\\Users\\Viktor\\Desktop\\artist.ttl"), "TURTLE");
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        System.out.println("\n\n********\n\n");
//
//        String resURL = URL.replace("data", "resource");
//        resURL = resURL.replace(".ttl", "");
//
//        Resource performer = model.getResource(resURL);
//
//        String bio = performer.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString();
//        //  System.out.println(bio);
//
//        String comment = performer.getProperty(RDFS.comment, "en").getObject().toString();
//
//        String startYear = performer.getProperty(new PropertyImpl("http://dbpedia.org/ontology/activeYearsStartYear")).getObject().toString();
//
//        String actualStartYear = String.valueOf(startYear.charAt(0)) +
//                startYear.charAt(1) +
//                startYear.charAt(2) +
//                startYear.charAt(3);
//
//
//        String actualEndYear = "";
//        String endYear;
//        if (performer.hasProperty(new PropertyImpl("http://dbpedia.org/ontology/activeYearsEndYear"))) {
//            endYear = performer.getProperty(new PropertyImpl("http://dbpedia.org/ontology/activeYearsEndYear")).getObject().toString();
//
//            actualEndYear = String.valueOf(endYear.charAt(0)) +
//                    endYear.charAt(1) +
//                    endYear.charAt(2) +
//                    endYear.charAt(3);
//        }
//
//        StmtIterator activeMembers = performer.listProperties(new PropertyImpl("http://dbpedia.org/ontology/bandMember"));
//        if(activeMembers.hasNext()){
//            while (activeMembers.hasNext()){
//                Statement member = activeMembers.nextStatement();
//                //System.out.println(member.getObject().toString());
//                String fullUrl = member.getObject().toString();
//                String[] ss = fullUrl.split("/");
//                activeMembersToSend.add(ss[ss.length-1].replace("_", " "));
//            }
//        }
//
//        StmtIterator pastMembers = performer.listProperties(new PropertyImpl("http://dbpedia.org/ontology/formerBandMember"));
//        if(pastMembers.hasNext()){
//            while (pastMembers.hasNext()){
//                Statement member = pastMembers.nextStatement();
//                //System.out.println(member.getObject().toString());
//                String fullUrl = member.getObject().toString();
//                String[] ss = fullUrl.split("/");
//                pastMembersToSend.add(ss[ss.length-1].replace("_", " "));
//            }
//        }
//
//        StmtIterator genres = performer.listProperties(new PropertyImpl("http://dbpedia.org/ontology/genre"));
//        if(genres.hasNext()){
//            while (genres.hasNext()){
//                Statement genre = genres.nextStatement();
//                // System.out.println(genre.getObject().toString());
//                String fullUrl = genre.getObject().toString();
//                String[] ss = fullUrl.split("/");
//                genresToSend.add(ss[ss.length-1].replace("_", " "));
//            }
//        }
//
//        StmtIterator recordLabels = performer.listProperties(new PropertyImpl("http://dbpedia.org/ontology/recordLabel"));
//        if(recordLabels.hasNext()){
//            while (recordLabels.hasNext()){
//                Statement recordLabel = recordLabels.nextStatement();
//                //System.out.println(recordLabel.getObject().toString());
//                String fullUrl = recordLabel.getObject().toString();
//                String[] ss = fullUrl.split("/");
//                recordLabelsToSend.add(ss[ss.length-1].replace("_", " "));
//            }
//        }
//
//        String picture = performer.getProperty(FOAF.depiction).getObject().toString();
//
//
//        String homepage = "None";
//        if(performer.hasProperty(FOAF.homepage))
//            homepage = performer.getProperty(FOAF.homepage).getObject().toString();
//
//        System.out.println(actualStartYear + " - " + actualEndYear);
//        System.out.println(comment);
//        System.out.println("Active Members:\n" + activeMembersToSend);
//        System.out.println("Former Members:\n" + pastMembersToSend);
//        System.out.println("Genres:\n" + genresToSend);
//        System.out.println("Record Labels:\n" + recordLabelsToSend);
//        System.out.println("Picture:\n" + picture);
//        System.out.println("Homepage:\n" + homepage);
//
//        System.out.println("\n*****\n");
//        SPARQL(artist);
//    }
//
//
//    public static void SPARQL(String artist) throws IOException {
//        String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
//                "PREFIX dbp: <http://dbpedia.org/property/>\n" +
//                "PREFIX dbr: <http://dbpedia.org/resource/>" +
//                "select distinct ?album_name ?year\n" +
//                "where{\n" +
//                "?album dbo:artist dbr:ARTIST.\n" +
//                "?album dbp:thisAlbum ?album_name.\n" +
//                "?album dbo:releaseDate ?year\n" +
//                "}";
//        queryString = queryString.replace("ARTIST", artist);
//        Query query = QueryFactory.create(queryString);
//        QueryExecution queryExecution = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
//        ResultSet resultSet = queryExecution.execSelect();
//        System.out.println("Albums:\n");
//        while(resultSet.hasNext()){
//            QuerySolution querySolution = resultSet.nextSolution();
//            String[] ss = querySolution.toString().split("\"");
//            System.out.println(ss[1]);
//        }
//    }
    }
}

