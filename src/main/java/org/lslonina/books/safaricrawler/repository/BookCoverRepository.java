package org.lslonina.books.safaricrawler.repository;

import org.lslonina.books.safaricrawler.model.cover.BookCover;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookCoverRepository extends MongoRepository<BookCover, String> {

}
