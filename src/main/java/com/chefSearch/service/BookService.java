package com.chefSearch.service;

import com.chefSearch.model.Book;
import org.apache.jena.rdf.model.Resource;

import java.util.List;

public interface BookService {
    void createBooks(Resource bookResource, Book bookModel, List<Book> books);
}