package org.lslonina.books.safaricrawler.crawler;

import org.lslonina.books.safaricrawler.dto.Book;
import org.lslonina.books.safaricrawler.dto.BookBuilder;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.model.generic.QueryResult;
import org.lslonina.books.safaricrawler.model.generic.SafariBook;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class Crawler {
    private static final Logger log = LoggerFactory.getLogger(Crawler.class);
    public static final String BASE = "https://learning.oreilly.com/api/v2/search/";
    public static final String LIMIT = "100";
    public static final String SORT_BY_DATE_ADDED = "date_added";
    public static final String SORT_BY_PUBLICATION_DATE = "publication_date";
    public static final String SORT_BY = SORT_BY_DATE_ADDED;

    private static String createQueryBooksAddress(int page) {
        return BASE + "?sort=" + SORT_BY + "&query=*&limit=" + LIMIT + "&include_case_studies=true&include_courses=true&include_orioles=true&include_playlists=true&include_collections=true&collection_type=expert&collection_sharing=public&collection_sharing=enterprise&exclude_fields=description&page=" + page + "&formats=book";
    }

    private final RestTemplate restTemplate;

    private final SafariBookRepository safariBookRepository;
    private final SafariBookDetailsRepository safariBookDetailsRepository;
    private final BookRepository bookRepository;

    public Crawler(RestTemplate restTemplate, SafariBookRepository safariBookRepository, SafariBookDetailsRepository safariBookDetailsRepository, BookRepository bookCoverRepository) {
        this.restTemplate = restTemplate;
        this.safariBookRepository = safariBookRepository;
        this.safariBookDetailsRepository = safariBookDetailsRepository;
        this.bookRepository = bookCoverRepository;
    }

    public void loadData() {
        List<SafariBook> safariBooks = getSafariBooks(0);
        safariBookRepository.saveAll(safariBooks);

        List<BookDetails> bookDetails = getBookDetails(safariBooks);
        safariBookDetailsRepository.saveAll(bookDetails);

        List<Book> books = createBooks(safariBooks, bookDetails);
        bookRepository.saveAll(books);
    }

    private List<BookDetails> getBookDetails(List<SafariBook> safariBooks) {
        return safariBooks.stream().map(safariBook -> {
            log.info("Fetching details for: " + safariBook.getTitle());
            BookDetails details = restTemplate.getForObject(safariBook.getUrl(), BookDetails.class);
            log.info("Fetched details for: " + safariBook.getTitle());
            return details;
        }).collect(Collectors.toList());
    }

    private List<SafariBook> getSafariBooks(int page) {
        String address = createQueryBooksAddress(page);
        log.info("Fetching: " + address);
        List<SafariBook> safariBooks = restTemplate.getForObject(address, QueryResult.class).getSafariBooks();
        log.info("Fetched: " + safariBooks.size());
        return safariBooks;
    }

    private List<Book> createBooks(List<SafariBook> safariBooks, List<BookDetails> bookDetails) {
        Map<String, BookDetails> bookDetailsMap = bookDetails.stream()
                .collect(Collectors.toMap(BookDetails::getIdentifier, details -> details));

        return safariBooks.stream().map(safariBook -> createBook(safariBook, bookDetailsMap.get(safariBook.getArchiveId()), getCover(safariBook))).collect(Collectors.toList());
    }

    private Book createBook(SafariBook safariBook, BookDetails details, String coverData) {
        Date dateAdded = Date.from(Instant.parse(safariBook.getDateAdded()));
        Date datePublished = Date.from(Instant.parse(safariBook.getIssued()));

        BookBuilder bookBuilder = new BookBuilder(safariBook.getArchiveId())
                .withTitle(safariBook.getTitle())
                .withAuthors(safariBook.getAuthors())
                .withDescription(details.getDescription())
                .withPages(details.getPagecount())
                .withCover(coverData)
                .withPriority(0)
                .withAdded(dateAdded)
                .withPublished(datePublished);
        return bookBuilder.build();
    }

    private String getCover(SafariBook safariBook) {
        log.info("Fetching cover for: " + safariBook.getTitle());
        byte[] imageBytes = restTemplate.getForObject(safariBook.getCoverUrl(), byte[].class);
        String imageAsString = Base64.getEncoder().encodeToString(imageBytes);
        if (imageAsString.isBlank()) {
            log.warn("Can't fetch cover for: " + safariBook.getCoverUrl());
        }
        log.info("Fetched cover for: " + safariBook.getCoverUrl());
        return imageAsString;
    }

    private String getKey(SafariBook safariBook) {
        List<String> publishers = safariBook.getPublishers();
        return publishers == null ? "Unknown" : publishers.get(0);
    }
}
