package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Allure;

import java.util.ArrayList;
import java.util.List;

public class EbayProductPage {
    private final Page page;
    private final Locator productTitle;
    private final Locator productPrice;
    private final Locator relatedSectionHeading;
    private final Locator relatedProducts;
    private final Locator relatedProductLinks;
    private final Locator breadcrumbCategories;
    private final Locator mainProductPrice;
    private final Locator relatedProductPrices;
    private final Locator relatedProductsGrid;

    public EbayProductPage(Page page) {

        this.page = page;
        Allure.step("Initializing EbayProductPage...");

        this.productTitle = page.locator("h1.x-item-title__mainTitle span");
        this.productPrice = page.locator(".x-price-primary span");
        this.relatedSectionHeading = page.locator("//h2[contains(text(),'Similar')]");
        this.relatedProductsGrid = page.locator("div[style*='grid-template-columns']:has(section)");
        this.relatedProducts = relatedProductsGrid.locator("section");
        this.relatedProductLinks = page.locator("//h2[normalize-space()='Similar items']/following::a[contains(@href,'/itm/')][@aria-label]");
        this.breadcrumbCategories = page.locator("//a[contains(@class,'seo-breadcrumb-text')]/span");
        this.mainProductPrice = page.locator("[data-testid='ux-textual-display'] .ux-textspans--SECONDARY.ux-textspans--BOLD");
        this.relatedProductPrices = page.locator("//h2[text()='Similar items']/ancestor::section[1]//div[contains(@class,'iALQ')]/span");
    }

    public void waitForPageLoad() {
        Allure.step("Waiting for product page load");
        productTitle.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(30000));
        Allure.step("SUCCESS: Product page loaded");
    }

    public void scrollToRelatedProducts() {
        Allure.step("Scrolling to Similar Items section");
        relatedSectionHeading.waitFor(new Locator.WaitForOptions().setTimeout(60000));
        relatedSectionHeading.scrollIntoViewIfNeeded();
        Allure.step("Reached Similar Items section");
    }

    public boolean isRelatedProductsSectionVisible() {

        try {
            scrollToRelatedProducts();
            boolean visible = relatedSectionHeading.isVisible();
            Allure.step("Related Products section visible: " + visible);
            return visible;

        } catch (TimeoutError e) {
            Allure.step("Related Products section NOT visible");
            return false;
        }
    }

    public int getRelatedProductCount() {
        relatedProducts.first().waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(30000));
        int count = relatedProducts.count();
        Allure.step("Related Products Count: " + count);
        return count;
    }

    public String getProductCategory() {
        breadcrumbCategories.first().waitFor();
        String category = breadcrumbCategories.last().textContent().trim();
        Allure.step("Product Category: " + category);
        return category;
    }

    public List<String> getRelatedProductUrls() {
        int count = relatedProductLinks.count();
        Allure.step("Related products found: " + count);
        List<String> urls = new ArrayList<>();

        for (int i = 0; i < Math.min(count, 6); i++) {
            String url = relatedProductLinks.nth(i).getAttribute("href");
            Allure.step("Related URL " + (i + 1) + ": " + url);
            if (url != null)
                urls.add(url);
        }

        return urls;
    }
    public boolean isMainProductInRelatedProducts() {

        String mainTitle = productTitle.textContent().trim();
        Allure.step("Main Product Title: " + mainTitle);

        int count = relatedProducts.count();
        Allure.step("Total related products: " + count);

        for (int i = 0; i < count; i++) {

            String relatedTitle = relatedProducts.nth(i)
                            .locator("h3")
                            .textContent()
                            .trim();
            Allure.step("Related Product " + (i + 1) + " Title: " + relatedTitle);
            System.out.println("Related Product " + (i + 1) + " Title: " + relatedTitle);

            if (relatedTitle.equalsIgnoreCase(mainTitle)) {

                Allure.step("Error: Main product FOUND inside related products");
                System.out.println("ERROR: Main product exists in related list");

                return true;
            }
        }

        Allure.step("Main product NOT included in related products");

        return false;
    }

    public void navigateAndVerifyAllRelatedProducts() {

        int count = relatedProductLinks.count();
        Allure.step("Total related products to verify: " + count);
        System.out.println("Total related products = " + count);

        for (int i = 0; i < count; i++) {

            final int index = i;

            Allure.step("Opening Related Product #" + (index + 1));
            System.out.println("Opening related product tab -> " + (index + 1));

            Page relatedTab =
                    page.waitForPopup(() ->
                            relatedProductLinks.nth(index).click()
                    );

            Allure.step("New tab opened");

            EbayProductPage relatedProductPage = new EbayProductPage(relatedTab);

            relatedProductPage.waitForPageLoad();
            relatedProductPage.verifyProductDetailsDisplayed();
            Allure.step("Closing Related Product #" + (index + 1));

            relatedTab.close();
            page.bringToFront();
            Allure.step("Returned to main product page");
        }

        Allure.step("All related products verified successfully");
    }
    public double getMainProductPrice() {
        String priceText = mainProductPrice.textContent().replaceAll("[^\\d.]", "");
        Allure.step("Main Product Price: $" + priceText);
        return Double.parseDouble(priceText);
    }

    public List<String> getOutOfRangeRelatedProductPrices(int count) {

        double mainPrice = getMainProductPrice();
        double minPrice = mainPrice * 0.8;
        double maxPrice = mainPrice * 1.2;

        Allure.step("Main product price: $"+mainPrice);
        System.out.println("Main product price: $"+mainPrice);
        Allure.step("Allowed price range: $" + String.format("%.2f", minPrice) + " - $" + String.format("%.2f", maxPrice));
        System.out.println("Allowed price range: $" + String.format("%.2f", minPrice) + " - $" + String.format("%.2f", maxPrice));

        List<String> outOfRange = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            String priceText = relatedProductPrices.nth(i).textContent().trim();
            priceText = priceText.replace("$", "").replace(",", "").trim();
            double relatedPrice = Double.parseDouble(priceText);

            if (relatedPrice < minPrice || relatedPrice > maxPrice) {
                Allure.step("Out of range: $" + relatedPrice);
                System.out.println("Out of range: $" + relatedPrice);
                outOfRange.add(priceText);
            }
        }

        return outOfRange;
    }

    public void verifyProductDetailsDisplayed() {

        Allure.step("Verifying product title visibility");
        productTitle.waitFor();

        org.testng.Assert.assertTrue(productTitle.isVisible(), "Product title not visible");

        System.out.println("Title: " + productTitle.textContent());
        Allure.step("Title: " + productTitle.textContent());

        Allure.step("Verifying product price visibility");
        productPrice.waitFor();

        org.testng.Assert.assertTrue(productPrice.isVisible(), "Product price not visible");

        System.out.println("Price: " + productPrice.textContent());
        Allure.step("Price: " + productPrice.textContent());

        Allure.step("SUCCESS: Product details verified");
    }
}