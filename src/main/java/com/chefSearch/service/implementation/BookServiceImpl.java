package com.chefSearch.service.implementation;

import com.chefSearch.model.Book;
import com.chefSearch.service.BookService;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Override
    public void createBooks(Resource bookResource, Book bookModel, List<Book> books) {
        String bookName = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/property/name")).getObject().toString().replace("@en", "");
        String bookDescription = bookResource.getProperty(new PropertyImpl("http://dbpedia.org/ontology/abstract"), "en").getObject().toString().replace("@en", "");
        String published = "";
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
}