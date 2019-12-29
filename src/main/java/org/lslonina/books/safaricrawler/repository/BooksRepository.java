package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.generic.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BooksRepository extends MongoRepository<Book, String> {
    List<Book> findByTitleContainingIgnoringCase(String title);

    Optional <Book> findByIsbn(String isbn);

    List<Book> findAllByPriorityNot(int priority);
}