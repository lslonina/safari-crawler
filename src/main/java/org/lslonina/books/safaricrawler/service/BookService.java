package org.lslonina.books.safaricrawler.service;

import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Date;
import java.util.List;

public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by("added").descending());
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

    public Page<Book> findAllSelected() {
        return bookRepository.findAllByPriorityGreaterThan(0, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("modificationTimestamp").descending()));
    }

    public Page<Book> findUnprocessed(PageRequest pageRequest) {
        return bookRepository.findAllByPriorityEquals(0, pageRequest);
    }

    public Book findById(String id) {
        return bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public void skip(String id) {
        updateBookPriority(id, -1);
    }

    public void select(String id) {
        updateBookPriority(id, 1);
    }

    private void updateBookPriority(String id, int priority) {
        Book book = findById(id);
        book.setModificationTimestamp(new Date());
        book.setPriority(priority);
        bookRepository.save(book);
    }
}
