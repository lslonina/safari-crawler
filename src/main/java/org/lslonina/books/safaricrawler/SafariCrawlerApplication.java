package org.lslonina.books.safaricrawler;

import org.lslonina.books.safaricrawler.model.Book;
import org.lslonina.books.safaricrawler.model.QueryResult;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@SpringBootApplication
public class SafariCrawlerApplication {

    private static final Logger log = LoggerFactory.getLogger(SafariCrawlerApplication.class);
    public static final String BASE = "https://learning.oreilly.com/api/v2/search/";
    public static final String ADDRESS = BASE + "?sort=publication_date&query=*&limit=36&include_case_studies=true&include_courses=true&include_orioles=true&include_playlists=true&include_collections=true&collection_type=expert&collection_sharing=public&collection_sharing=enterprise&exclude_fields=description&page=1&formats=book";
    public static final String LOGIN_ENTRY_URL = "https://learning.oreilly.com/login/unified/?next=/home/";

    public static void main(String[] args) {
        SpringApplication.run(SafariCrawlerApplication.class, args);
    }

    @Value("${crawler.user}")
    private String user;

    @Value("${crawler.password}")
    private String password;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) throws IOException {
        return restTemplateBuilder
                .basicAuthentication(user, password)
                .defaultHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .defaultHeader("accept-encoding", "gzip, deflate")
                .defaultHeader("origin", "https://learning.oreilly.com")
                .defaultHeader("referer", "https://learning.oreilly.com/login/unified/?next=/home/")
                .defaultHeader("upgrade-insecure-requests", "1")
                .defaultHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.113 Safari/537.36")
                .defaultHeader("cookie", readCookies())
                .build();

    }

    private static String readCookies() throws IOException {
        InputStream resourceAsStream = SafariCrawlerApplication.class.getResourceAsStream("/cookie.txt");
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (resourceAsStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }

    @Bean
    public CommandLineRunner run(RestTemplate restTemplate, BooksRepository booksRepository) throws Exception {
        return args -> {
            booksRepository.deleteAll();

            log.info("Fetching: " + ADDRESS);
            QueryResult queryResult = restTemplate.getForObject(ADDRESS, QueryResult.class);
            log.info("Fetched: " + queryResult.getPage());
            booksRepository.saveAll(queryResult.getBooks());

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
                break;
            }
        };
    }

    private String getKey(Book book) {
        List<String> publishers = book.getPublishers();
        return publishers == null ? "Unknown" : publishers.get(0);
    }
}