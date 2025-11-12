package cz.uhk.zlesak.threejslearningapp.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

/**
 * Custom I18NProvider for providing Czech translations.
 * It loads translations from a properties file located at "texts/pages_cs.properties".
 */
@Slf4j
@Component
public class CustomI18NProvider implements I18NProvider {
    private final Map<String, String> csTranslations = new HashMap<>();
    private final ObjectMapper mapper;

    @Autowired
    public CustomI18NProvider(ObjectMapper mapper) {
        this.mapper = mapper;
        loadAllCsJson();
    }

    private void loadAllCsJson() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

        try {
            Resource[] resources = resolver.getResources("classpath*:texts/*_cs.json");
            List<Resource> sorted = Arrays.asList(resources);

            sorted.sort(Comparator.comparing(r -> Optional.ofNullable(r.getFilename()).orElse("")));

            for (Resource resource : sorted) {
                String filename = resource.getFilename();
                if (filename == null) {
                    continue;
                }
                try (InputStream input = resource.getInputStream()) {
                    Map<String, String> map = mapper.readValue(input, new TypeReference<>() {});
                    for (String key : map.keySet()) {
                        if (csTranslations.containsKey(key)) {
                            log.debug("Duplicitní klíč '{}' v {} – přepisuje se novou hodnotou", key, filename);
                        }
                    }
                    csTranslations.putAll(map);
                } catch (Exception e) {
                    log.error("Chyba při čtení souboru {}: {}", filename, e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("Chyba při vyhledávání *_cs.json souborů: {}", e.getMessage(), e);
        }
    }

    /**
     * Returns the translation for the given key and locale.
     * If the locale is Czech (cs), it looks up the translation in the loaded properties file.
     * If the translation is found, it returns the translated string; otherwise, it returns the key itself as a fallback.
     *
     * @param key    translation key
     * @param locale locale to use
     * @param params parameters used in translation string
     * @return the translated string if found, otherwise the key itself
     */
    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (locale.getLanguage().equals("cs")) {
            String value = csTranslations.get(key);
            if (value != null) {
                return value;
            }
        }
        log.warn("Překlad nenalezen pro klíč: {} v jazyce: {}", key, locale.getLanguage());
        return key;
    }

    /**
     * Returns a list of locales that this provider supports.
     * In this case, it only supports Czech (cs) for now.
     *
     * @return a list containing the Czech locale (for now, may change in the future if more languages are needed)
     */
    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(Locale.forLanguageTag("cs"));
    }
}
