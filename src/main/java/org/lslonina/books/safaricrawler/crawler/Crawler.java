package org.lslonina.books.safaricrawler.crawler;

import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.dto.BookBuilder;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.model.generic.QueryResult;
import org.lslonina.books.safaricrawler.model.generic.SafariBook;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.SafariBooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Crawler {
    private static final Logger log = LoggerFactory.getLogger(Crawler.class);

    public static final String BASE = "https://learning.oreilly.com/api/v2/search/";
    public static final String ADDRESS = BASE + "?sort=publication_date&query=*&limit=36&include_case_studies=true&include_courses=true&include_orioles=true&include_playlists=true&include_collections=true&collection_type=expert&collection_sharing=public&collection_sharing=enterprise&exclude_fields=description&page=1&formats=book";

    private final RestTemplate restTemplate;

    private final SafariBooksRepository safariBooksRepository;
    private final SafariBookDetailsRepository safariBookDetailsRepository;
    private final BookRepository bookRepository;

    public Crawler(RestTemplate restTemplate, SafariBooksRepository safariBooksRepository, SafariBookDetailsRepository safariBookDetailsRepository, BookRepository bookCoverRepository) {
        this.restTemplate = restTemplate;
        this.safariBooksRepository = safariBooksRepository;
        this.safariBookDetailsRepository = safariBookDetailsRepository;
        this.bookRepository = bookCoverRepository;
    }

    public void loadData() {
        log.info("Fetching: " + ADDRESS);
        List<SafariBook> safariBooks = restTemplate.getForObject(ADDRESS, QueryResult.class).getSafariBooks();
        safariBooks.forEach(b -> b.setPriority(0));
        log.info("Fetched: " + safariBooks.size());

        safariBooksRepository.saveAll(safariBooks);


        Map<String, BookDetails> bookDetailsMap = safariBooks.stream().map(safariBook -> {
            log.info("Fetching details for: " + safariBook.getTitle());
            BookDetails bookDetails = restTemplate.getForObject(safariBook.getUrl(), BookDetails.class);
            log.info("Fetched details for: " + safariBook.getTitle());
            return bookDetails;
        }).collect(Collectors.toMap(BookDetails::getIdentifier, details -> details));

        safariBookDetailsRepository.saveAll(bookDetailsMap.values());

        List<Book> books = createBooks(safariBooks, bookDetailsMap);
        bookRepository.saveAll(books);
    }

    private List<Book> createBooks(List<SafariBook> safariBooks, Map<String, BookDetails> bookDetailsMap) {
        List<Book> books = new ArrayList<>(safariBooks.size());
        safariBooks.forEach(safariBook -> {
            log.info("Fetching cover for: " + safariBook.getTitle());
            byte[] imageBytes = restTemplate.getForObject(safariBook.getCoverUrl(), byte[].class);
            String imageAsString = Base64.getEncoder().encodeToString(imageBytes);
            if (imageAsString.isBlank()) {
                log.warn("Can't fetch cover for: " + safariBook.getTitle());
            }
            log.info("Fetched cover for: " + safariBook.getTitle());

            BookDetails bookDetails = bookDetailsMap.get(safariBook.getArchiveId());
            BookBuilder bookBuilder = new BookBuilder(safariBook.getArchiveId())
                    .withTitle(safariBook.getTitle())
                    .withAuthors(safariBook.getAuthors())
                    .withDescription(bookDetails.getDescription())
                    .withPages(bookDetails.getPagecount())
                    .withCover(imageAsString)
                    .withPriority(0);
            books.add(bookBuilder.build());
        });
        return books;
    }

    private String getKey(SafariBook safariBook) {
        List<String> publishers = safariBook.getPublishers();
        return publishers == null ? "Unknown" : publishers.get(0);
    }
}
