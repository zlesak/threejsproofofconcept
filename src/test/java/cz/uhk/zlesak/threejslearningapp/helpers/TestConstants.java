package cz.uhk.zlesak.threejslearningapp.helpers;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Konstanty pro testy
 */
public class TestConstants {
    public static final String BASE_URL = "http://localhost:8081";
    public static final Path TEST_FILES_DIR = Paths.get("src/test/resources/test-data/models");

    public static class AdminCredentials {
        public static final String USERNAME = "admin";
        public static final String PASSWORD = "admin";
    }

    public static class Routes {
        public static final String HOME = "/";
        public static final String LOGIN = "/login";
        public static final String CREATE_MODEL = "/createModel";
        public static final String CREATE_CHAPTER = "/createChapter";
        public static final String MODELS = "/models";
        public static final String CHAPTERS = "/chapters";
    }
}

