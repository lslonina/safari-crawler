package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.Book;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookDetailsRepository extends MongoRepository<BookDetails, String> {
    Optional<BookDetails> findByIsbn(String isbn);
}