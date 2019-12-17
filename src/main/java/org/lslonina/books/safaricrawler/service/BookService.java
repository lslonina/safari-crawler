package org.lslonina.books.safaricrawler.service;


import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.lslonina.books.safaricrawler.model.cover.BookCover;
import org.lslonina.books.safaricrawler.model.generic.Book;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.repository.BookCoverRepository;
import org.lslonina.books.safaricrawler.repository.BookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookService {

    private final BooksRepository booksRepository;
    private final BookDetailsRepository bookDetailsRepository;
    private final BookCoverRepository bookCoverRepository;

    public BookService(BooksRepository booksRepository, BookDetailsRepository bookDetailsRepository, BookCoverRepository bookCoverRepository) {
        this.booksRepository = booksRepository;
        this.bookDetailsRepository = bookDetailsRepository;
        this.bookCoverRepository = bookCoverRepository;
    }

    @GetMapping("/books")
    public List<Book> all() {
        return booksRepository.findAll();
    }

    @GetMapping("/books/{isbn}")
    BookDetails one(@PathVariable String isbn) {
        return bookDetailsRepository.findByIsbn(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @GetMapping(value = "/books/{isbn}/cover")
    public ResponseEntity<byte[]> getImage(@PathVariable("isbn") String isbn) {
        BookCover bookCover = bookCoverRepository.findById(isbn).orElseThrow(() -> new BookNotFoundException(isbn));
        String cover = bookCover.getCover();
        byte[] image = Base64.getDecoder().decode(cover);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
    }
}