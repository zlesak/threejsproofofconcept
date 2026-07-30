package cz.uhk.zlesak.threejslearningapp.backend.service;

import cz.uhk.zlesak.threejslearningapp.backend.persistence.FullTextDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Keeps a searchable text projection of chapters and answers keyword queries against it.
 * The index is created with language {@code none}, so Czech text is matched literally instead of
 * being stemmed by English rules.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FullTextSearchService {

    private final MongoTemplate mongoTemplate;

    /**
     * Creates the text index once the application is up. A failure here degrades search but must not
     * stop the application from starting.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndex() {
        try {
            mongoTemplate.indexOps(FullTextDocument.class).createIndex(TextIndexDefinition.builder()
                    .onField("text")
                    .withDefaultLanguage("none")
                    .build());
        } catch (RuntimeException e) {
            log.warn("Fulltextový index se nepodařilo vytvořit, vyhledávání nemusí fungovat: {}", e.getMessage());
        }
    }

    /**
     * Indexes an entity, replacing any previous entry for it.
     *
     * @param externalId id of the indexed entity.
     * @param type       kind of entity.
     * @param texts      texts to index; blank values are skipped.
     */
    public void index(String externalId, FullTextDocument.FullTextType type, String... texts) {
        String combined = combine(texts);
        if (externalId == null || combined.isBlank()) {
            return;
        }

        mongoTemplate.upsert(
                new Query(Criteria.where("externalId").is(externalId)),
                new Update().set("text", combined).set("type", type),
                FullTextDocument.class);
    }

    /**
     * Drops an entity from the index, so deleted entities stop appearing in search results.
     *
     * @param externalId id of the entity.
     */
    public void remove(String externalId) {
        if (externalId == null) {
            return;
        }
        mongoTemplate.remove(new Query(Criteria.where("externalId").is(externalId)), FullTextDocument.class);
    }

    /**
     * @param keyword text typed by the user.
     * @param type    kind of entity to search.
     * @return ids of the matching entities.
     */
    public List<String> search(String keyword, FullTextDocument.FullTextType type) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        Query query = new Query(TextCriteria.forDefaultLanguage().matching(keyword));
        query.addCriteria(Criteria.where("type").is(type));

        return mongoTemplate.find(query, FullTextDocument.class).stream()
                .map(FullTextDocument::getExternalId)
                .filter(Objects::nonNull)
                .toList();
    }

    private String combine(String... texts) {
        if (texts == null) {
            return "";
        }
        return String.join(" ", java.util.Arrays.stream(texts)
                .filter(text -> text != null && !text.isBlank())
                .toList());
    }
}
