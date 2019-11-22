package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BooksRepository extends MongoRepository<Book, String> {
    List<Book> findByTitleContainingIgnoringCase(String title);
}