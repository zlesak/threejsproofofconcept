package cz.uhk.zlesak.threejslearningapp.tests.models;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.*;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import cz.uhk.zlesak.threejslearningapp.helpers.TestConstants;
import cz.uhk.zlesak.threejslearningapp.tests.bases.PlaywrightTestBase;
import org.junit.jupiter.api.*;

import static cz.uhk.zlesak.threejslearningapp.helpers.TestConstants.BASE_URL;
import static cz.uhk.zlesak.threejslearningapp.helpers.TestConstants.TEST_FILES_DIR;

@UsePlaywright
public class ModelUploadAdvancedTest extends PlaywrightTestBase {
    private static final  Path FEMUR_DIR = TEST_FILES_DIR.resolve("femur");
    private static final Path MODEL_FILE = FEMUR_DIR.resolve("femur.obj");
    private static final Path MAIN_TEXTURE = FEMUR_DIR.resolve("femur.1001.jpg");
    private static final Path OTHER_TEXTURE_1 = FEMUR_DIR.resolve("femur.1001-parts1.jpg");
    private static final Path OTHER_TEXTURE_2 = FEMUR_DIR.resolve("femur.1001-parts2.jpg");
    private static final Path CSV_FILE_1 = FEMUR_DIR.resolve("femur.1001-parts1.csv");
    private static final Path CSV_FILE_2 = FEMUR_DIR.resolve("femur.1001-parts2.csv");

    @Test
    @DisplayName("Pokročilé nahrání modelu")
    void test() {
        String modelName = modelName();

        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Nahrát model")).click();
        page.getByLabel("", new Page.GetByLabelOptions().setExact(true)).fill(modelName);
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Pokročilé nahrání modelu")).check();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nahrát soubor (.obj)")).click();

        FileChooser fileChooserModel = page.waitForFileChooser(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nahrát soubor (.obj)")).click());
        fileChooserModel.setFiles(MODEL_FILE);

        FileChooser fileChooserTexture = page.waitForFileChooser(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nahrát soubor (.jpg)")).nth(0).click());
        fileChooserTexture.setFiles(MAIN_TEXTURE);

        FileChooser fileChooserOtherTextures = page.waitForFileChooser(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nahrát soubor (.jpg)")).nth(1).click());
        fileChooserOtherTextures.setFiles(new Path[]{OTHER_TEXTURE_1, OTHER_TEXTURE_2});

        FileChooser fileChooserCSV = page.waitForFileChooser(() -> page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Nahrát soubor (.csv)")).click());
        fileChooserCSV.setFiles(new Path[]{CSV_FILE_1, CSV_FILE_2});

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Vytvořit model")).click();

        page.navigate(BASE_URL + TestConstants.Routes.MODELS);

        page.waitForSelector("text=" + modelName, new Page.WaitForSelectorOptions().setTimeout(5000));
        Assertions.assertTrue(page.getByText(modelName).isVisible());
    }

    private String modelName() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
        return "Femur Test Model " + timestamp;
    }
}
