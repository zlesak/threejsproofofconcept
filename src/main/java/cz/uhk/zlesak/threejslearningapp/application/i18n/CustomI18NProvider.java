package cz.uhk.zlesak.threejslearningapp.application.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Custom I18NProvider for providing Czech translations.
 * It loads translations from a properties file located at "texts/pages_cs.properties".
 */
@Slf4j
@Component
public class CustomI18NProvider implements I18NProvider {
    private final Map<String, String> csTranslations;

    /**
     * Constructor for CustomI18NProvider.
     * It loads the Czech translations from the properties file.
     * If the file is not found or an error occurs during loading, it logs an error message.
     * The properties file should be located in the classpath under texts/ directory with the name ending in "_cs.properties".
     * For example: texts/pages_cs.properties.
     */
    public CustomI18NProvider() {
        csTranslations = new HashMap<>();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL textsDir = classLoader.getResource("texts");
            if (textsDir != null) {
                loadTranslationsFromResource(classLoader);
            } else {
                log.warn("Texts file nenalezeno");
            }
        } catch (Exception e) {
            log.error("Chyba při načítání překladů z JSON: {}", e.getMessage(), e);
        }
    }

    private void loadTranslationsFromResource(ClassLoader classLoader) {
        ObjectMapper mapper = new ObjectMapper();

        String[] translationFiles = {
                "beforeLeaveActionDialog_cs.json",
                "chapter_cs.json",
                "generic_cs.json",
                "pages_cs.json",
                "uppload_component_cs.json"
        };

        for (String fileName : translationFiles) {
            try (InputStream input = classLoader.getResourceAsStream("texts/" + fileName)) {
                if (input != null) {
                    Map<String, String> map = mapper.readValue(input, new TypeReference<>() {});
                    csTranslations.putAll(map);
                } else {
                    log.warn("Nenalezeno: {}", fileName);
                }

            } catch (Exception e) {
                log.error("Chyba při načítání českých překladů z JSON: {}", e.getMessage(), e);
            }
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
            if (csTranslations != null) {
                String value = csTranslations.get(key);
                if (value != null) {
                    return value;
                }
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
