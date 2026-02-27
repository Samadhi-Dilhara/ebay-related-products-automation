package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;

public class EbayHomePage {
    private final Page page;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final Locator searchInput;
    private final Locator searchButton;
    private final Locator ebayLogo;

    public EbayHomePage(Page page) {
        this.page = page;
        this.searchInput = page.locator("#gh-ac");
        this.searchButton = page.locator("//*[@id=\"gh-search-btn\"]");
        this.ebayLogo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("eBay Home"));
    }

    // ── Actions ───────────────────────────────────────────────────────────────
    public void navigateTo(String url) {
        Allure.step("Navigating to eBay home: {}" +url);
        page.navigate(url);
        page.waitForSelector("#gh-ac",
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
        Allure.step("eBay home page loaded.");
    }

    public void searchForProduct(String keyword) {
        Allure.step("Searching for: {}"+keyword);
        searchInput.fill(keyword);
        searchButton.click();
        page.waitForURL("**/sch/**");
        Allure.step("Search results page loaded.");
    }

    // ── Verifications ─────────────────────────────────────────────────────────
    public boolean isHomePageLoaded() {
        return ebayLogo.isVisible();
    }

    public boolean isSearchInputVisible() {
        return searchInput.isVisible();
    }
}