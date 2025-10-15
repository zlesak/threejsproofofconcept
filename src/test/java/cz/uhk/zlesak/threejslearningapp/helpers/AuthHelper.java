package cz.uhk.zlesak.threejslearningapp.helpers;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;

import static cz.uhk.zlesak.threejslearningapp.helpers.TestConstants.*;

/**
 * Helper třída pro autentizaci v testech
 */
public class AuthHelper {

    public static void login(Page page) {
        login(page, AdminCredentials.USERNAME, AdminCredentials.PASSWORD);
    }

    public static void login(Page page, String username, String password) {

        page.navigate(BASE_URL + Routes.LOGIN);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Uživatelské jméno")).fill(username);
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Heslo")).fill(password);
        page.locator("//*[@id=\"vaadinLoginFormWrapper\"]/vaadin-button[contains(text(),'Přihlásit se')]").click();
        PlaywrightAssertions.assertThat(page).hasURL(BASE_URL + Routes.HOME);
    }
}

