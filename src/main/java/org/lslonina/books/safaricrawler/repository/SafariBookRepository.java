package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.generic.SafariBook;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SafariBookRepository extends MongoRepository<SafariBook, String> {
}