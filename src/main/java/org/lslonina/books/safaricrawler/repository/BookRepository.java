package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.dto.Book;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    List<Book> findAllByPriorityEquals(int priority);

    List<Book> findAllByPriorityLessThan(int priority);

    List<Book> findAllByPriorityGreaterThan(int priority);
}
