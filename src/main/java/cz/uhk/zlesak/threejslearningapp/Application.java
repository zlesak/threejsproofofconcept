package cz.uhk.zlesak.threejslearningapp;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import cz.uhk.zlesak.threejslearningapp.application.i18n.CustomI18NProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main application class for the Three.js Learning App.
 * It sets up the Spring Boot application and provides RestTemplate and I18NProvider beans.
 * The application uses a custom theme named "threejslearningapp".
 */
@SpringBootApplication(scanBasePackages = "cz.uhk.zlesak.threejslearningapp")
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
    public CustomI18NProvider customI18NProvider() {
        return new CustomI18NProvider();
    }

    /**
     * Configures the application shell settings.
     * This method sets the favicon for the application as of now.
     *
     * @param settings initial application shell settings
     */
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon", "icons/MISH_icon.ico", "256x256");
    }
}