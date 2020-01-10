package org.lslonina.books.safaricrawler.api;


import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookApi {
    private static final Logger log = LoggerFactory.getLogger(BookApi.class);

    private final BookService bookService;

    public BookApi(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<Book> bookList(@RequestParam String filter) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("added").ascending());
        Page<Book> result;
        if (filter.equals("all")) {
            result = bookService.findAll(pageRequest);
        } else if (filter.equals("skipped")) {
            result = bookService.findSkipped(pageRequest);
        } else if (filter.equals("selected")) {
            result = bookService.findSelected(pageRequest);
        } else if (filter.equals("unprocessed")) {
            result = bookService.findUnprocessed(pageRequest);
        } else {
            throw new RuntimeException("Query param not supported: " + filter);
        }
        return result.toList();
    }

    @GetMapping("/books/{id}")
    public Book one(@PathVariable String id) {
        log.info("Getting details for: " + id);
        if (!id.equals("undefined")) {
            return bookService.findById(id);
        }
        return null;
    }

    @PostMapping("/books/{id}/skip")
    public void skip(@PathVariable String id) {
        log.info("Skipping: " + id);
        if (!id.equals("undefined")) {
            bookService.skip(id);
        }
        log.info("Skipped: " + id);
    }

    @PostMapping("/books/{id}/select")
    public void select(@PathVariable String id) {
        log.info("Selecting: " + id);
        if (!id.equals("undefined")) {
            bookService.select(id);
        }
        log.info("Selected: " + id);
    }

    @GetMapping(value = "/books/{id}/cover")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") String id) {
        log.info("Getting cover for: " + id);
        if (!id.equals("undefined")) {
            Book book = bookService.findById(id);
            String cover = book.getCover();
            byte[] image = Base64.getDecoder().decode(cover);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        }
        return null;
    }
}