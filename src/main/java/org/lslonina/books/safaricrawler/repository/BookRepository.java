package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.dto.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {
    Page<Book> findAllByPriorityEquals(int priority, Pageable pageable);

    Page<Book> findAllByPriorityLessThan(int priority, Pageable pageable);

    Page<Book> findAllByPriorityGreaterThan(int priority, Pageable pageable);
}
