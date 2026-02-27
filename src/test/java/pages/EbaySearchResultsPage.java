package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;

public class EbaySearchResultsPage {
    private final Page page;
    private final Locator allResultCards;
    private final Locator allResultLinks;
    private final Locator resultsCountHeading;
    private final Locator selectedProductFromSearch;

    public EbaySearchResultsPage(Page page) {
        this.page = page;
        this.allResultCards = page.locator("#srp-river-results > ul");
        this.allResultLinks = page.locator("li.s-item .s-item__link");
        this.resultsCountHeading = page.locator(".srp-controls__count-heading");
        this.selectedProductFromSearch = page.locator("//li[@id='item1d40be8452']/div[@class='su-card-container su-card-container--vertical']/div[@class='su-card-container__media']/div[@class='s-card__media-wrapper']/div[@class='su-media su-media--image']/div[@class='su-media__primary']/div[@class='su-media__image']/a[@class='s-card__link image-treatment']");
    }

    public void waitForResultsToLoad() {
        Allure.step("Waiting for search results to load...");
        page.waitForSelector("#srp-river-results > ul",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(50000));
        Allure.step("Search results loaded.");
    }

    public boolean isResultsPageLoaded() {
        return allResultCards.count() > 0;
    }

    public String getResultsCountText() {
        try { return resultsCountHeading.textContent().trim(); }
        catch (Exception e) { return ""; }
    }

    public void clickItemById(String itemId) {
        Allure.step("Looking for item ID {} in search results..."+itemId);
        waitForResultsToLoad();

        if (tryClickItem(itemId)) return;

        for (int pg = 2; pg <= 5; pg++) {
            Locator nextBtn = page.locator(
                    "a.pagination__next, " +
                            "nav.pagination a[aria-label='Go to next search page']"
            ).first();

            if (nextBtn.count() == 0 || !nextBtn.isEnabled()) {
                Allure.step("No more pages --- item {} not found."+itemId);
                break;
            }

            Allure.step("Item not on page " + (pg - 1) + " --- going to page " + pg + "...");
            nextBtn.click();
            waitForResultsToLoad();

            if (tryClickItem(itemId)) return;
        }

        throw new RuntimeException(
                "Target item ID '" + itemId + "' was not found in the first 5 pages of search results."
        );
    }
    public void clickSelectedProduct() {
        waitForResultsToLoad();
        selectedProductFromSearch.click();
    }

    private boolean tryClickItem(String itemId) {
        Locator target = page.locator(
                "li.s-item .s-item__link[href*='" + itemId + "']"
        ).first();

        if (target.count() > 0) {
            Allure.step("Found item {} --- clicking."+itemId);
            target.scrollIntoViewIfNeeded();
            target.click();
            page.waitForURL("**/itm/**");
            Allure.step("Product page loaded for item {}."+itemId);
            return true;
        }
        return false;
    }

}