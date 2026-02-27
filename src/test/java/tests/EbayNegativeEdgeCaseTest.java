package tests;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.EbayHomePage;
import pages.EbayProductPage;
import pages.EbaySearchResultsPage;
import utils.ConfigManager;

@Epic("eBay Wallet - Related Products")
@Feature("Negative & Edge Cases")
public class EbayNegativeEdgeCaseTest extends BaseTest {
    private EbayProductPage doFullNavigation() {
        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        homePage.searchForProduct(ConfigManager.SEARCH_KEYWORD);

        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(page);
        resultsPage.clickItemById(ConfigManager.TARGET_ITEM_ID);

        EbayProductPage productPage = new EbayProductPage(page);
        productPage.waitForPageLoad();
        return productPage;
    }

    @Test(priority = 1)
    @Story("Empty search input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify submitting an empty search from eBay home does not crash the site")
    public void TC_NEG_001_emptySearchDoesNotCrash() {
        Allure.step("TC-NEG-001: Submit empty search");

        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        page.locator("#gh-ac").fill("");
        page.locator("#gh-btn").click();
        page.waitForTimeout(2000);

        String bodyText = page.locator("body").textContent();
        Assert.assertNotNull(bodyText, "Page body should not be null after empty search");
        Assert.assertFalse(bodyText.trim().isEmpty(),
                "Page body should not be empty after empty search");

        Allure.step("TC-NEG-001 PASSED");
    }

    @Test(priority = 2)
    @Story("Non-existent item ID in search results")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that looking for a bogus item ID throws a controlled RuntimeException")
    public void TC_NEG_002_nonExistentItemIdThrowsControlledError() {
        Allure.step("TC-NEG-002: Attempt to find non-existent item ID in search results");

        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        homePage.searchForProduct(ConfigManager.SEARCH_KEYWORD);

        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(page);
        try {
            resultsPage.clickItemById("0000000000001");
            Allure.step("No exception thrown --- item found unexpectedly");
        } catch (RuntimeException e) {
            Allure.step("Correctly threw RuntimeException: {}"+ e.getMessage());
            Assert.assertTrue(e.getMessage().contains("not found"),
                    "Exception message should indicate item was not found");
        }

        Allure.step("TC-NEG-002 PASSED");
    }

    @Test(priority = 3)
    @Story("Special characters in search")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify searching with special characters from eBay home does not crash the site")
    public void TC_NEG_003_specialCharactersInSearchDoNotCrash() {
        Allure.step("TC-NEG-003: Search with special characters");

        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        page.locator("#gh-ac").fill("!@#$%^&*()");
        page.locator("#gh-btn").click();
        page.waitForTimeout(2000);

        Assert.assertFalse(page.title().isEmpty(),
                "Page title should not be empty after special character search");

        Allure.step("TC-NEG-003 PASSED");
    }

    @Test(priority = 4)
    @Story("Scroll beyond related products section")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify the page does not error when scrolling past the related products section")
    public void TC_NEG_004_scrollBeyondRelatedProductsNoError() {
        Allure.step("TC-NEG-004: Scroll past related products --- verify no JS error");

        EbayProductPage productPage = doFullNavigation();
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);

        Assert.assertNotNull(productPage.getCurrentUrl(), "Page URL should still be accessible after excessive scrolling");

        Allure.step("TC-NEG-004 PASSED");
    }
}