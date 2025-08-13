package cz.uhk.zlesak.threejslearningapp.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Custom I18NProvider for providing Czech translations.
 * It loads translations from a properties file located at "texts/pages_cs.properties".
 */
@Slf4j
@Component
public class CustomI18NProvider implements I18NProvider {
    private final Properties csProperties = new Properties();

    /**
     * Constructor for CustomI18NProvider.
     * It loads the Czech translations from the properties file.
     * If the file is not found or an error occurs during loading, it logs an error message.
     * The properties file should be located in the classpath under "texts/pages_cs.properties
     */
    public CustomI18NProvider() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("texts/pages_cs.properties")) {
            if (input != null) {
                csProperties.load(input);
            }
        } catch (IOException e) {
            log.error("Chyba při načítání českých překladů: {}", e.getMessage(), e);
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
            String value = csProperties.getProperty(key);
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
        return List.of(new Locale("cs"));
    }
}
