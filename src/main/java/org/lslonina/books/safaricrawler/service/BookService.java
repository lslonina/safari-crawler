package org.lslonina.books.safaricrawler.service;


import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.repository.BookRepository;
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

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public List<Book> bookList(@RequestParam String filter) {
        List<Book> result;
        if (filter.equals("all")) {
            result = bookRepository.findAll();
        } else if (filter.equals("skipped")) {
            result = bookRepository.findAllByPriorityLessThan(0);
        } else if (filter.equals("selected")) {
            result = bookRepository.findAllByPriorityGreaterThan(0);
        } else if (filter.equals("unprocessed")) {
            result = bookRepository.findAllByPriorityEquals(0);
        } else {
            throw new RuntimeException("Query param not supported: " + filter);
        }
        return result;
    }

    @GetMapping("/books/{id}")
    public Book one(@PathVariable String id) {
        log.info("Getting details for: " + id);
        if (!id.equals("undefined")) {
            return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        }
        return null;
    }

    @PostMapping("/books/{id}/skip")
    public void skip(@PathVariable String id) {
        log.info("Skipping: " + id);
        if (!id.equals("undefined")) {
            Optional<Book> book = bookRepository.findById(id);
            if (book.isPresent()) {
                Book bookResult = book.get();
                bookResult.setPriority(-1);
                bookRepository.save(bookResult);
                log.info("Skipped: " + id);
            }
        }
    }

    @GetMapping(value = "/books/{id}/cover")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        log.info("Getting cover for: " + id);
        if (!id.equals("undefined")) {
            Book book = bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
            String cover = book.getCover();
            byte[] image = Base64.getDecoder().decode(cover);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        }
        return null;
    }
}