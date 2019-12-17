package org.lslonina.books.safaricrawler.model.cover;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;

public class BookCover {
    @Id
    @JsonProperty("identifier")
    private String identifier;

    @JsonProperty("cover")
    private String cover;

    public BookCover() {
    }

    public BookCover(String identifier, String cover) {
        this.identifier = identifier;
        this.cover = cover;
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
}
