package org.lslonina.books.safaricrawler.dto;

import java.util.List;

public class BookBuilder {
    private final String identifier;
    private String title;
    private List<String> authors;
    private String description;
    private int pages;
    private String cover;
    private int priority;

    public BookBuilder(String identifier) {
        this.identifier = identifier;
    }

    public BookBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public BookBuilder withAuthors(List<String> authors) {
        this.authors = authors;
        return this;
    }

    public BookBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public BookBuilder withPages(int pages) {
        this.pages = pages;
        return this;
    }

    public BookBuilder withCover(String cover) {
        this.cover = cover;
        return this;
    }

    public BookBuilder withPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public Book build() {
        return new Book(identifier, title, authors, description, pages, cover, priority);
    }
}