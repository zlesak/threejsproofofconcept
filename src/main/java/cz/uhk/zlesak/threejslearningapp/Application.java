package cz.uhk.zlesak.threejslearningapp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.theme.Theme;
import cz.uhk.zlesak.threejslearningapp.i18n.CustomI18NProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the Three.js Learning App.
 * It sets up the Spring Boot application and provides RestTemplate and I18NProvider beans.
 * The application uses a custom theme named "threejslearningapp".
 */
@SpringBootApplication
@Theme(value = "threejslearningapp")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public I18NProvider i18nProvider() {
        return new CustomI18NProvider();
    }
}