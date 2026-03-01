package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;

public class EbayHomePage {
    private final Page page;
    private final Locator searchInput;
    private final Locator searchButton;
    private final Locator ebayLogo;

    public EbayHomePage(Page page) {
        this.page = page;
        this.searchInput = page.locator("#gh-ac");
        this.searchButton = page.locator("#gh-search-btn");
        this.ebayLogo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("eBay Home"));
    }

    public void navigateTo(String url) {
            Allure.step("Navigating to URL: " + url);

            page.navigate(url);

            Allure.step("Waiting for search input visibility...");

            page.waitForSelector("#gh-ac", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));

            Allure.step("SUCCESS: eBay Home Page Loaded");
    }

    public void searchForProduct(String keyword) {
        Allure.step("Searching product: " + keyword);

        searchInput.fill(keyword);
        Allure.step("Keyword entered.");

        searchButton.click();
        Allure.step("Search button clicked.");

        page.waitForURL("**/sch/**");

        Allure.step("SUCCESS: Search Results Page Loaded");
    }
}