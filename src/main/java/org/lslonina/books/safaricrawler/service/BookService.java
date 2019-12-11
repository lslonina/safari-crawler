package org.lslonina.books.safaricrawler.service;


import org.lslonina.books.safaricrawler.model.Book;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.repository.BookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookService {

    private final BooksRepository booksRepository;
    private BookDetailsRepository bookDetailsRepository;

    public BookService(BooksRepository booksRepository, BookDetailsRepository bookDetailsRepository) {
        this.booksRepository = booksRepository;
        this.bookDetailsRepository = bookDetailsRepository;
    }

    @GetMapping("/books")
    public List<Book> all() {
        return booksRepository.findAll();
    }

    @GetMapping("/books/{id}")
    BookDetails one(@PathVariable String id) {
        return bookDetailsRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}