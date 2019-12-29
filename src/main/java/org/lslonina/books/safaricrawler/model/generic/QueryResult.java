
package org.lslonina.books.safaricrawler.model.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "results",
        "facets",
        "total",
        "meta",
        "page",
        "previous",
        "next"
})
public class QueryResult {

    @JsonProperty("results")
    private List<SafariBook> safariBooks = null;
    @JsonProperty("facets")
    private Facets facets;
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("meta")
    private Meta meta;
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("previous")
    private String previous;
    @JsonProperty("next")
    private String next;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("results")
    public List<SafariBook> getSafariBooks() {
        return safariBooks;
    }

    @JsonProperty("results")
    public void setSafariBooks(List<SafariBook> safariBooks) {
        this.safariBooks = safariBooks;
    }

    @JsonProperty("facets")
    public Facets getFacets() {
        return facets;
    }

    @JsonProperty("facets")
    public void setFacets(Facets facets) {
        this.facets = facets;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("meta")
    public Meta getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    @JsonProperty("page")
    public Integer getPage() {
        return page;
    }

    @JsonProperty("page")
    public void setPage(Integer page) {
        this.page = page;
    }

    @JsonProperty("previous")
    public String getPrevious() {
        return previous;
    }

    @JsonProperty("previous")
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    @JsonProperty("next")
    public String getNext() {
        return next;
    }

    @JsonProperty("next")
    public void setNext(String next) {
        this.next = next;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
