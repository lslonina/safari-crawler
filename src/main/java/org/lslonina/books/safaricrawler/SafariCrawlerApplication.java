package org.lslonina.books.safaricrawler;

import org.lslonina.books.safaricrawler.crawler.Crawler;
import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class SafariCrawlerApplication {
    private static final Logger log = LoggerFactory.getLogger(SafariCrawlerApplication.class);
    private static final String ID_PREFIX = "https://www.safaribooksonline.com/api/v1/book/";

    public static void main(String[] args) {
        SpringApplication.run(SafariCrawlerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(SafariBookRepository safariBookRepository, SafariBookDetailsRepository bookDetailsRepository, BookRepository bookRepository, Crawler crawler) throws Exception {
        return args -> {
//            export(bookRepository);
//            deleteData(safariBookRepository, bookDetailsRepository, bookRepository);
            fetchData(safariBookRepository, bookDetailsRepository, bookRepository, crawler);
        };
    }

    private void fetchData(SafariBookRepository safariBookRepository, SafariBookDetailsRepository bookDetailsRepository, BookRepository bookRepository, Crawler crawler) {
        try {
            crawler.loadData();
        } catch (RuntimeException ex) {
            log.info("Can't load data", ex);
        }
    }

    private void export(BookRepository bookRepository) {
        List<Book> all = bookRepository.findAll();
        List<Book> selected = all.stream().filter(b -> b.getPriority() > 0).collect(Collectors.toList());

        List<Book> ignored = all.stream().filter(b -> b.getPriority() < 0).collect(Collectors.toList());
        List<Book> unprocessed = all.stream().filter(b -> b.getPriority() == 0).collect(Collectors.toList());

        export(selected, "selected");
        export(ignored, "ignored");
        export(unprocessed, "unprocessed");
        System.out.println("Exported.");
    }

    private void export(List<Book> books, String prefix) {
        String isbn = books.stream().map(book -> book.getIsbn() + "\n").collect(Collectors.joining());
        String id = books.stream().map(book -> book.getIdentifier() + "\n").collect(Collectors.joining());
        try {
            Files.write(Path.of(prefix + "-isbn.csv"), isbn.trim().getBytes());
            Files.write(Path.of(prefix + "-id.csv"), id.trim().getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Wasn't able to store file: " + prefix);
        }
    }

    private void deleteData(SafariBookRepository safariBookRepository, SafariBookDetailsRepository safariBookDetailsRepository, BookRepository bookRepository) {
        safariBookDetailsRepository.deleteAll();
        safariBookRepository.deleteAll();
        bookRepository.deleteAll();
        System.out.println("Deleted.");
    }
}