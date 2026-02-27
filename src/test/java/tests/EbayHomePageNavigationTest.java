package tests;

import base.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.EbayHomePage;
import pages.EbaySearchResultsPage;
import utils.ConfigManager;

@Epic("eBay Wallet - Related Products")
@Feature("Navigation")
public class EbayHomePageNavigationTest extends BaseTest {

    // TC-NAV-003
    @Test(priority = 3)
    @Story("Target item found in search results and clicked")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify item 125640279122 can be found in search results and opens the correct product page")
    public void TC_NAV_003_findAndClickTargetItemFromSearchResults() {
        Allure.step("TC-NAV-003: Find item {} in search results"+ConfigManager.TARGET_ITEM_ID);

        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        homePage.searchForProduct(ConfigManager.SEARCH_KEYWORD);

        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(page);
        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Search results should show item cards after searching 'wallet'");
        Assert.assertTrue(page.url().contains("sch") || page.url().contains("wallet"),
                "URL should reflect a search results page. Actual: " + page.url());

        Allure.step("Results count: {}"+ resultsPage.getResultsCountText());
        resultsPage.clickSelectedProduct();

        Allure.step("TC-NAV-003 PASSED");
    }
}