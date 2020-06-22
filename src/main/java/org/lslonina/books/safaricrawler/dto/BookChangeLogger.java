package org.lslonina.books.safaricrawler.dto;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.*;

public class BookChangeLogger {
    private static final Logger log = LoggerFactory.getLogger(BookChangeLogger.class);

    public static <T> void logChanges(List<T> books) {
        if (books.size() < 2) {
            throw new RuntimeException("At least two objects required for comparision.");
        }
        T base = books.get(0);
        for (T book : books) {
            if (book != base) {
                logChanges(base, book);
            }
        }
        System.out.println("");
    }

    public static <T> void logChanges(T base, T other) {
        if (base == null) {
            log.info("Base book: " + other);
            return;
        }
        log.info("Changed book: " + other);
        ObjectDiffer objectDiffer = ObjectDifferBuilder.buildDefault();
        DiffNode root = objectDiffer.compare(other, base);
        root.visit((node, visit) -> {
            printDiff(base, other, node);
        });
    }

    private static <T> void printDiff(T baseObject, T other, DiffNode node) {
        Collection<String> toIgnore = asList("description", "cover", "topicsPayload", "topics", "popularity", "averageRating", "reportScore", "numberOfReviews");
        String nodeString = node.getPath().toString();
        if (toIgnore.stream().anyMatch(nodeString::contains)) {
            return;
        }
        final Object baseValue = node.canonicalGet(baseObject);
        final Object workingValue = node.canonicalGet(other);
        System.out.println(" " + nodeString);
        System.out.println("  " + baseValue);
        System.out.println("  " + workingValue);
    }
}
