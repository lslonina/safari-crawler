package org.lslonina.books.safaricrawler.service;


import org.lslonina.books.safaricrawler.model.cover.BookCover;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.model.generic.Book;
import org.lslonina.books.safaricrawler.repository.BookCoverRepository;
import org.lslonina.books.safaricrawler.repository.BookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    private final BooksRepository booksRepository;
    private final BookDetailsRepository bookDetailsRepository;
    private final BookCoverRepository bookCoverRepository;

    public BookService(BooksRepository booksRepository, BookDetailsRepository bookDetailsRepository, BookCoverRepository bookCoverRepository) {
        this.booksRepository = booksRepository;
        this.bookDetailsRepository = bookDetailsRepository;
        this.bookCoverRepository = bookCoverRepository;
    }

    @GetMapping("/books")
    public List<Book> allNotSkipped() {
        return booksRepository.findAllByPriorityNot(-1);
    }

    @GetMapping("/books/{isbn}")
    public BookDetails one(@PathVariable String isbn) {
        log.info("Getting details for: " + isbn);
        if (!isbn.equals("undefined")) {
            return bookDetailsRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        }
        return null;
    }

    @PostMapping("/books/{isbn}/skip")
    public void skip(@PathVariable String isbn) {
        log.info("Skipping: " + isbn);
        if (!isbn.equals("undefined")) {
            Optional<Book> book = booksRepository.findByIsbn(isbn);
            if (book.isPresent()) {
                Book bookResult = book.get();
                bookResult.setPriority(-1);
                booksRepository.save(bookResult);
                log.info("Skipped: " + isbn);
            }
        }
    }

    @GetMapping(value = "/books/{isbn}/cover")
    public ResponseEntity<byte[]> getImage(@PathVariable("isbn") String isbn) {
        log.info("Getting cover for: " + isbn);
        if (!isbn.equals("undefined")) {
            BookCover bookCover = bookCoverRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
            String cover = bookCover.getCover();
            byte[] image = Base64.getDecoder().decode(cover);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        }
        return null;
    }
}