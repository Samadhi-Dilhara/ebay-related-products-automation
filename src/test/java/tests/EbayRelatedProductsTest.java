package tests;

import base.BaseTest;
import com.microsoft.playwright.Page;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.EbayHomePage;
import pages.EbayProductPage;
import pages.EbaySearchResultsPage;
import utils.ConfigManager;

import java.util.List;

/**
 * TC-RP-001 to TC-RP-006
 *
 * Flow: Home → Search "wallet" → Click selected product → Assert related products section
 *
 * All tests share the same navigation setup via @BeforeMethod.
 */
@Epic("eBay Wallet - Related Products")
@Feature("Related Products Section")
public class EbayRelatedProductsTest extends BaseTest {

    private EbayProductPage productPage;

    // ── Shared setup ──────────────────────────────────────────────────────────
    @BeforeMethod(alwaysRun = true)
    public void navigateToProduct() {
        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        homePage.searchForProduct(ConfigManager.SEARCH_KEYWORD);

        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(page);
        resultsPage.waitForResultsToLoad();
        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Search results should show item cards after searching 'wallet'");

        Page productTab = page.waitForPopup(() -> resultsPage.clickSelectedProduct());
        productPage = new EbayProductPage(productTab);
        productPage.waitForPageLoad();
    }

    // ── TC01 ──────────────────────────────────────────────────────────────────
    @Test(priority = 1)
    @Story("Related products section is visible")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify Related Products Section Visibility")
    public void TC01_verifyRelatedProductsSectionVisible() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");
    }

    // ── TC02 ──────────────────────────────────────────────────────────────────
    @Test(priority = 2)
    @Story("Related product count does not exceed 6")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that at most 6 related products are displayed (per spec)")
    public void TC02_verifyRelatedProductCountMaxSix() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");

        int relatedCount = productPage.getRelatedProductCount();
        Assert.assertTrue(relatedCount <= 6,
                "Related products exceed 6! Actual count: " + relatedCount);
    }

    // ── TC03 ──────────────────────────────────────────────────────────────────
    @Test(priority = 3)
    @Story("Related products belong to same category")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify related products belong to same category as main product")
    public void TC03_verifySameCategoryRelatedProducts() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");

        String mainCategory = productPage.getProductCategory();
        Assert.assertNotNull(mainCategory, "Main product category not found");

        List<String> relatedUrls = productPage.getRelatedProductUrls();
        Assert.assertTrue(relatedUrls.size() > 0, "No related products found");

        for (String url : relatedUrls) {
            page.navigate(url);
            EbayProductPage relatedPage = new EbayProductPage(page);
            String relatedCategory = relatedPage.getProductCategory();
            Allure.step("Main: {} | Related: {}"+ mainCategory+ relatedCategory);
            Assert.assertEquals(relatedCategory, mainCategory, "Category mismatch detected!");
        }
    }

    // ── TC04 ──────────────────────────────────────────────────────────────────
    @Test(priority = 4)
    @Story("Related product prices within allowed range")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify related products prices are within the allowed range of the main product")
    public void TC04_verifyRelatedProductPriceRange() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");

        int relatedCount = productPage.getRelatedProductCount();
        List<String> outOfRange = productPage.getOutOfRangeRelatedProductPrices(relatedCount);

        Assert.assertEquals(outOfRange.size(), 0,
                "Some related products are out of the allowed price range! Out-of-range prices: " + outOfRange);
    }

    // ── TC05 ──────────────────────────────────────────────────────────────────
    @Test(priority = 5)
    @Story("Main product not shown in related products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the main product does not appear under the related products section")
    public void TC05_verifyMainProductNotInRelatedProducts() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");

        Assert.assertFalse(productPage.isMainProductInRelatedProducts(),
                "Main product should NOT appear under related products.");
    }

    // ── TC06 ──────────────────────────────────────────────────────────────────
    @Test(priority = 6)
    @Story("User can navigate to related product pages")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify user can navigate to a related product page and see product details")
    public void TC06_verifyNavigationToRelatedProductPages() {
        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(),
                "Related products section should be visible");

        productPage.navigateAndVerifyAllRelatedProducts();
        productPage.waitForPageLoad();
        productPage.verifyProductDetailsDisplayed();
    }
}