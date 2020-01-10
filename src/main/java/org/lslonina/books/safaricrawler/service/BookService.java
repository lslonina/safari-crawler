package org.lslonina.books.safaricrawler.service;

import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Page<Book> findAll(PageRequest pageRequest) {
        return bookRepository.findAll(pageRequest);
    }

    public Page<Book> findSkipped(PageRequest pageRequest) {
        return bookRepository.findAllByPriorityLessThan(0, pageRequest);
    }

    public Page<Book> findSelected(PageRequest pageRequest) {
        return bookRepository.findAllByPriorityGreaterThan(0, pageRequest);
    }

    public Page<Book> findUnprocessed(PageRequest pageRequest) {
        return bookRepository.findAllByPriorityEquals(0, pageRequest);
    }

    public Book findById(String id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public void skip(String id) {
        Book book = findById(id);
        book.setPriority(-1);
        bookRepository.save(book);
    }

    public void select(String id) {
        Book book = findById(id);
        book.setPriority(1);
        bookRepository.save(book);
    }
}
