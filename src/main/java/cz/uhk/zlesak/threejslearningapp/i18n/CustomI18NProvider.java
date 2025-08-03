package cz.uhk.zlesak.threejslearningapp.i18n;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Slf4j
@Component
public class CustomI18NProvider implements I18NProvider {
    private final Properties csProperties = new Properties();

    public CustomI18NProvider() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("texts/pages_cs.properties")) {
            if (input != null) {
                csProperties.load(input);
            }
        } catch (IOException e) {
            log.error("Chyba při načítání českých překladů: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (locale.getLanguage().equals("cs")) {
            String value = csProperties.getProperty(key);
            if (value != null) {
                return value;
            }
        }
        return key;
    }

    @Override
    public List<Locale> getProvidedLocales() {
        return List.of(new Locale("cs"));
    }
}
