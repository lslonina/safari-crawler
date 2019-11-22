package org.lslonina.books.safaricrawler;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.lslonina.books.safaricrawler.model.Book;
import org.lslonina.books.safaricrawler.model.QueryResult;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@SpringBootApplication
public class SafariCrawlerApplication {

    private static final Logger log = LoggerFactory.getLogger(SafariCrawlerApplication.class);
    public static final String BASE = "https://learning.oreilly.com/api/v2/search/";
    public static final String ADDRESS = BASE + "?sort=date_added&query=*&limit=36&include_case_studies=true&include_courses=true&include_orioles=true&include_playlists=true&include_collections=true&collection_type=expert&collection_sharing=public&collection_sharing=enterprise&exclude_fields=description&page=1&formats=book";

    public static void main(String[] args) {
        SpringApplication.run(SafariCrawlerApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.basicAuthentication("", "").build();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate, BooksRepository booksRepository) throws Exception {
        return args -> {
            booksRepository.deleteAll();
            log.info("Fetching: " + ADDRESS);
            QueryResult queryResult = restTemplate.getForObject(ADDRESS, QueryResult.class);
            log.info("Fetched: " + queryResult.getPage());
            booksRepository.saveAll(queryResult.getBooks());

//            List<Book> searchResult = booksRepository.findByTitleContainingIgnoringCase("azure");
            List<Book> searchResult = booksRepository.findAll();

            Map<String, Set<Book>> books = searchResult.stream().collect(Collectors.groupingBy(book -> getKey(book), toSet()));

            for (Map.Entry<String, Set<Book>> bookEntrySet : books.entrySet()) {
                System.out.println(bookEntrySet.getKey());
                for (Book book : bookEntrySet.getValue()) {
                    System.out.println(" " + book.getTitle());
                    System.out.println("  " + book.getUrl());
                    BookDetails bookDetails = restTemplate.getForObject(book.getUrl(), BookDetails.class);
                    String description = bookDetails.getDescription();
                    System.out.println("  " + description);
                }
            }
        };
    }

    private String getKey(Book book) {
        List<String> publishers = book.getPublishers();
        return publishers == null ? "Unknown" : publishers.get(0);
    }
}