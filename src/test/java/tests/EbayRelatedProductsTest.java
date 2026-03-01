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

@Epic("eBay Wallet - Related Products")
@Feature("Related Products Section")
public class EbayRelatedProductsTest extends BaseTest {

    private EbayProductPage productPage;

    @BeforeMethod(alwaysRun = true)
    public void navigateToProduct() {

        Allure.step("Navigating to main product page");
        System.out.println("Navigating to main product page");

        EbayHomePage homePage = new EbayHomePage(page);
        homePage.navigateTo(ConfigManager.EBAY_HOME_URL);
        homePage.searchForProduct(ConfigManager.SEARCH_KEYWORD);

        EbaySearchResultsPage resultsPage = new EbaySearchResultsPage(page);
        resultsPage.waitForResultsToLoad();
        Assert.assertTrue(resultsPage.isResultsPageLoaded(), "Search results should show item cards after searching 'wallet'");

        System.out.println("Waiting for product popup tab");
        Page productTab = page.waitForPopup(() -> resultsPage.clickSelectedProduct());
        productPage = new EbayProductPage(productTab);
        productPage.waitForPageLoad();

        Allure.step("Main product page loaded successfully");
        System.out.println("Main product page loaded successfully");
    }

    @Test(priority = 1)
    @Story("Related products section is visible")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify Related Products Section Visibility")
    public void TC01_verifyRelatedProductsSectionVisible() {

        Allure.step("Verifying Related Products Section visibility");
        System.out.println("Verifying Related Products Section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        Allure.step("Related Products Section is visible");
        System.out.println("Related Products Section is visible");
    }

    @Test(priority = 2)
    @Story("Related product count does not exceed 6")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that at most 6 related products are displayed (per spec)")
    public void TC02_verifyRelatedProductCountMaxSix() {

        Allure.step("Verifying Related Products section visibility");
        System.out.println("Checking Related Products Section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        int relatedCount = productPage.getRelatedProductCount();

        Allure.step("Related products count: " + relatedCount);
        System.out.println("Related products count = " + relatedCount);

        Assert.assertTrue(relatedCount <= 6, "Related products exceed 6! Actual count: " + relatedCount);

        Allure.step("Related Products count verification passed");
        System.out.println("Related Products count verification passed");
    }

    @Test(priority = 3)
    @Story("Related products belong to same category")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify related products belong to same category as main product")
    public void TC03_verifySameCategoryRelatedProducts() {

        Allure.step("Verifying Related Products section visibility");
        System.out.println("Checking Related Products Section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        String mainCategory = productPage.getProductCategory();
        Allure.step("Main product category: " + mainCategory);
        System.out.println("Main product category = " + mainCategory);

        Assert.assertNotNull(mainCategory, "Main product category not found");

        List<String> relatedUrls = productPage.getRelatedProductUrls();
        Allure.step("Related products URLs count: " + relatedUrls.size());
        System.out.println("Related products found: " + relatedUrls.size());

        Assert.assertTrue(relatedUrls.size() > 0, "No related products found");

        for (String url : relatedUrls) {
            page.navigate(url);
            EbayProductPage relatedPage = new EbayProductPage(page);
            String relatedCategory = relatedPage.getProductCategory();

            String message = "Main: " + mainCategory + " | Related: " + relatedCategory;
            Allure.step(message);
            System.out.println(message);

            Assert.assertEquals(relatedCategory, mainCategory, "Category mismatch detected!");
        }

        Allure.step("Category verification for all related products passed");
        System.out.println("Category verification passed for all related products");
    }

    @Test(priority = 4)
    @Story("Related product prices within allowed range")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify related products prices are within the allowed range of the main product")
    public void TC04_verifyRelatedProductPriceRange() {

        Allure.step("Verifying Related Products section visibility");
        System.out.println("Checking Related Products Section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        int relatedCount = productPage.getRelatedProductCount();
        Allure.step("Total related products: " + relatedCount);
        System.out.println("Total related products = " + relatedCount);

        List<String> outOfRange = productPage.getOutOfRangeRelatedProductPrices(relatedCount);

        Allure.step("Out-of-range related product prices count: " + outOfRange.size());
        System.out.println("Out-of-range related product prices count = " + outOfRange.size());

        Assert.assertEquals(outOfRange.size(), 0, "Some related products are out of the allowed price range! Out-of-range prices: " + outOfRange);

        Allure.step("Related product price range verification passed");
        System.out.println("Related product price range verification passed");
    }

    @Test(priority = 5)
    @Story("Main product not shown in related products")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the main product does not appear under the related products section")
    public void TC05_verifyMainProductNotInRelatedProducts() {

        Allure.step("Verifying Related Products section visibility");
        System.out.println("Verifying Related Products section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        boolean mainInRelated = productPage.isMainProductInRelatedProducts();
        Allure.step("Main product in related products = " + mainInRelated);
        System.out.println("Main product in related products = " + mainInRelated);

        Assert.assertFalse(mainInRelated, "Main product should NOT appear under related products.");

        Allure.step("Main product not present in related products verified");
        System.out.println("Main product not present verification passed");
    }

    @Test(priority = 6)
    @Story("User can navigate to related product pages")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify user can navigate to a related product page and see product details")
    public void TC06_verifyNavigationToRelatedProductPages() {

        Allure.step("Verifying Related Products section visibility");
        System.out.println("Verifying Related Products section visibility");

        Assert.assertTrue(productPage.isRelatedProductsSectionVisible(), "Related products section should be visible");

        Allure.step("Navigating and verifying all related products pages");
        System.out.println("Navigating and verifying all related product pages");

        productPage.navigateAndVerifyAllRelatedProducts();

        productPage.waitForPageLoad();
        productPage.verifyProductDetailsDisplayed();

        Allure.step("Navigation and verification of related products completed");
        System.out.println("Navigation and verification of related products completed");
    }
}