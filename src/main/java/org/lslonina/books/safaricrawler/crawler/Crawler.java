package org.lslonina.books.safaricrawler.crawler;

import org.lslonina.books.safaricrawler.model.cover.BookCover;
import org.lslonina.books.safaricrawler.model.generic.Book;
import org.lslonina.books.safaricrawler.model.generic.QueryResult;
import org.lslonina.books.safaricrawler.model.details.BookDetails;
import org.lslonina.books.safaricrawler.repository.BookCoverRepository;
import org.lslonina.books.safaricrawler.repository.BookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.BooksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class Crawler {
    private static final Logger log = LoggerFactory.getLogger(Crawler.class);

    public static final String BASE = "https://learning.oreilly.com/api/v2/search/";
    public static final String ADDRESS = BASE + "?sort=publication_date&query=*&limit=36&include_case_studies=true&include_courses=true&include_orioles=true&include_playlists=true&include_collections=true&collection_type=expert&collection_sharing=public&collection_sharing=enterprise&exclude_fields=description&page=1&formats=book";

    private final RestTemplate restTemplate;

    private final BooksRepository booksRepository;
    private final BookDetailsRepository bookDetailsRepository;
    private final BookCoverRepository bookCoverRepository;

    public Crawler(RestTemplate restTemplate, BooksRepository booksRepository, BookDetailsRepository bookDetailsRepository, BookCoverRepository bookCoverRepository) {
        this.restTemplate = restTemplate;
        this.booksRepository = booksRepository;
        this.bookDetailsRepository = bookDetailsRepository;
        this.bookCoverRepository = bookCoverRepository;
    }

    public void loadData() {
        log.info("Fetching: " + ADDRESS);
        List<Book> books = restTemplate.getForObject(ADDRESS, QueryResult.class).getBooks();
        log.info("Fetched: " + books.size());
        List<BookCover> covers = getBookCovers(books);
        booksRepository.saveAll(books);
        bookCoverRepository.saveAll(covers);

        List<Book> searchResult = booksRepository.findAll();

        Map<String, Set<Book>> booksByPublishers = searchResult.stream().collect(Collectors.groupingBy(book -> getKey(book), toSet()));

        List<BookDetails> bookDetailsCollection = new ArrayList<>(searchResult.size());

        for (Map.Entry<String, Set<Book>> bookEntrySet : booksByPublishers.entrySet()) {
            System.out.println(bookEntrySet.getKey());
            for (Book book : bookEntrySet.getValue()) {
                BookDetails bookDetails = restTemplate.getForObject(book.getUrl(), BookDetails.class);
                String title = bookDetails.getTitle();
                String identifier = bookDetails.getIdentifier();
                System.out.println(" " + title + ": " + identifier);
                bookDetailsCollection.add(bookDetails);
            }
        }
        bookDetailsRepository.saveAll(bookDetailsCollection);
    }

    private List<BookCover> getBookCovers(List<Book> books) {
        List<BookCover> covers = new ArrayList<>(books.size());
        books.forEach(book -> {
            byte[] imageBytes = restTemplate.getForObject(book.getCoverUrl(), byte[].class);
            String imageAsString = Base64.getEncoder().encodeToString(imageBytes);
            if (imageAsString.isBlank()) {
                log.warn("Can't fetch cover for: " + book.getTitle());
            }
            covers.add(new BookCover(book.getIsbn(), imageAsString));
        });
        return covers;
    }

    private String getKey(Book book) {
        List<String> publishers = book.getPublishers();
        return publishers == null ? "Unknown" : publishers.get(0);
    }
}
