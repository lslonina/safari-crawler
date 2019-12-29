package org.lslonina.books.safaricrawler;

import org.lslonina.books.safaricrawler.crawler.Crawler;
import org.lslonina.books.safaricrawler.repository.BookRepository;
import org.lslonina.books.safaricrawler.repository.SafariBookDetailsRepository;
import org.lslonina.books.safaricrawler.repository.SafariBooksRepository;
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

@SpringBootApplication
public class SafariCrawlerApplication {
    private static final Logger log = LoggerFactory.getLogger(SafariCrawlerApplication.class);

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

    @Bean
    public Crawler crawler(RestTemplate restTemplate, SafariBooksRepository safariBooksRepository, SafariBookDetailsRepository safariBookDetailsRepository, BookRepository bookRepository) {
        return new Crawler(restTemplate, safariBooksRepository, safariBookDetailsRepository, bookRepository);
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
    public CommandLineRunner run(RestTemplate restTemplate, SafariBooksRepository safariBooksRepository, SafariBookDetailsRepository bookDetailsRepository, BookRepository bookRepository, Crawler crawler) throws Exception {
        return args -> {
            bookDetailsRepository.deleteAll();
            bookRepository.deleteAll();
            safariBooksRepository.deleteAll();
            crawler.loadData();
        };
    }
}