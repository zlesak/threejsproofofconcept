package cz.uhk.zlesak.threejslearningapp.tests.bases;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.management.ManagementFactory;
import java.util.Locale;

import static cz.uhk.zlesak.threejslearningapp.helpers.AuthHelper.login;

public abstract class PlaywrightTestBase {
    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        boolean debugProperty = Boolean.getBoolean("debug");
        String jvmArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().toLowerCase(Locale.ROOT);
        boolean jdwp = jvmArgs.contains("-agentlib:jdwp") || jvmArgs.contains("jdwp");
        boolean debug = debugProperty || jdwp;

        playwright = Playwright.create();
        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(!debug);
        browser = playwright.chromium().launch(options);

    }

    @AfterAll
    static void closeBrowser() {
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
        login(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
}
