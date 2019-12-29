package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.generic.SafariBook;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SafariBooksRepository extends MongoRepository<SafariBook, String> {
    List<SafariBook> findAllByPriorityEquals(int priority);

    List<SafariBook> findAllByPriorityLessThan(int priority);

    List<SafariBook> findAllByPriorityGreaterThan(int priority);
}