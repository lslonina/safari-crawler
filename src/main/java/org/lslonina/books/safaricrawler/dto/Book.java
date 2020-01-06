package org.lslonina.books.safaricrawler.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

public class Book {
    @Id
    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("author")
    private List<String> authors;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("pages")
    private int pages;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("priority")
    private int priority;

    @JsonProperty("added")
    private Date added;

    @JsonProperty("published")
    private Date published;

    public Book() {
    }

    public Book(String identifier, String title, List<String> authors, String description, int pages, String cover, int priority, Date added, Date published) {
        this.identifier = identifier;
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.pages = pages;
        this.cover = cover;
        this.priority = priority;
        this.added = added;
        this.published = published;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
