package org.lslonina.books.safaricrawler;

import org.lslonina.books.safaricrawler.crawler.Crawler;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SafariCrawlerApplication {
    private static final Logger log = LoggerFactory.getLogger(SafariCrawlerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SafariCrawlerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(SafariBookRepository safariBookRepository, SafariBookDetailsRepository bookDetailsRepository, BookRepository bookRepository, Crawler crawler) throws Exception {
        return args -> {
//            fetchData(safariBookRepository, bookDetailsRepository, bookRepository, crawler);
        };
    }

    private void fetchData(SafariBookRepository safariBookRepository, SafariBookDetailsRepository bookDetailsRepository, BookRepository bookRepository, Crawler crawler) {
        bookDetailsRepository.deleteAll();
        bookRepository.deleteAll();
        safariBookRepository.deleteAll();
        crawler.loadData();
    }
}