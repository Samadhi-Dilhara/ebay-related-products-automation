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

        Allure.step("Waiting for search results to load");

        page.waitForSelector(
                "#srp-river-results > ul",
                new Page.WaitForSelectorOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(50000)
        );

        Allure.step("SUCCESS: Search results loaded");
    }

    public boolean isResultsPageLoaded() {

        int count = allResultCards.count();

        Allure.step("Result containers found: " + count);
        System.out.println("Result container count = " + count);

        return count > 0;
    }

    public String getResultsCountText() {

        try {

            String text =
                    resultsCountHeading.textContent().trim();

            Allure.step("Results count text: " + text);
            System.out.println("Results count text = " + text);

            return text;

        } catch (Exception e) {

            Allure.step("ERROR: Unable to read results count");
            System.out.println("ERROR: Unable to read results count");

            return "";
        }
    }

    public void clickSelectedProduct() {

        waitForResultsToLoad();
        Allure.step("Clicking selected product from search results");
        selectedProductFromSearch.click();
        Allure.step("SUCCESS: Product clicked");
    }
}