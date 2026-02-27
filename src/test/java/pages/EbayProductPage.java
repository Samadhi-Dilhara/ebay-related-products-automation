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
    private final Locator addToCartButton;
    private final Locator productImageCarousel;
    private final Locator relatedSectionHeading;
    private Locator relatedProductsGrid;
    private Locator relatedProducts;
    private final Locator relatedProductLinks;
    private final Locator breadcrumbCategories;
    private Locator mainProductPrice;
    private Locator relatedProductPrices;

    public EbayProductPage(Page page) {
        this.page = page;
        this.productTitle = page.locator("h1.x-item-title__mainTitle span");
        this.productPrice = page.locator(".x-price-primary span");
        this.addToCartButton = page.locator("#atcBtn_btn, [data-testid='ux-call-to-action']");
        this.productImageCarousel = page.locator(".ux-image-carousel, #PicturePanel");
        this.relatedSectionHeading = page.locator("//h2[contains(text(),'Similar')]");
        this.relatedProductsGrid = page.locator("div[style*='grid-template-columns']:has(section)");
        this.relatedProducts = relatedProductsGrid.locator("section");
        this.relatedProductLinks = page.locator("//h2[normalize-space()='Similar items']/ancestor::section//a[contains(@class,'cFmJ') and contains(@href,'/itm/')]");
        this.breadcrumbCategories = page.locator("//a[contains(@class,'seo-breadcrumb-text')]/span");
        this.mainProductPrice = page.locator("[data-testid='ux-textual-display'] .ux-textspans--SECONDARY.ux-textspans--BOLD");
        this.relatedProductPrices = page.locator("//h2[text()='Similar items']/ancestor::section[1]//div[contains(@class,'v_81')]//div[contains(@class,'iALQ')]/span[@role='text']");
    }
    public void waitForPageLoad() {
        productTitle.waitFor(
                new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(30000));
        Allure.step("Product page loaded");
    }
    public String getCurrentUrl() {
        return page.url();
    }

    public void scrollToRelatedProducts() {
        Allure.step("Scrolling to Similar Items section...");
        relatedSectionHeading.waitFor(new Locator.WaitForOptions().setTimeout(60000));
        relatedSectionHeading.scrollIntoViewIfNeeded();
    }
    public boolean isRelatedProductsSectionVisible() {
        try {
            scrollToRelatedProducts();
            return relatedSectionHeading.isVisible();
        } catch (TimeoutError e) {
            Allure.step("❌ Related Products section not visible");
            return false;
        }
    }
    public int getRelatedProductCount() {
        relatedProducts.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(30000)
        );
        int count = relatedProducts.count();
        Allure.step("✅ Related Products Count = " + count);
        return count;
    }
    public String getProductCategory() {
        breadcrumbCategories.first().waitFor();
        String category = breadcrumbCategories.last().textContent().trim();
        Allure.step("Product Category: {}"+category);
        return category;
    }
    public List<String> getRelatedProductUrls() {
        int count = relatedProductLinks.count();
        Allure.step("Related products found: {}"+count);
        List<String> urls = new ArrayList<>();
        for (int i = 0; i < Math.min(count, 6); i++) {
            String url = relatedProductLinks.nth(i).getAttribute("href");
            Allure.step("Related Product URL: {}"+url);
            if (url != null)
                urls.add(url);
        }
        return urls;
    }
    public double getMainProductPrice() {
        String priceText = mainProductPrice.textContent().trim();
        priceText = priceText.replaceAll("[^\\d.]", "");
        Allure.step("Price Text: {}"+priceText);
        return Double.parseDouble(priceText);
    }
    public List<String> getOutOfRangeRelatedProductPrices(int count) {
        double mainPrice = getMainProductPrice();
        double minPrice = mainPrice * 0.8;
        double maxPrice = mainPrice * 1.2;

        Allure.step("Main product price: ${}"+mainPrice);
        Allure.step("Allowed range: ${} - ${}"+String.format("%.2f", minPrice)+String.format("%.2f", maxPrice));

        List<String> outOfRange = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String priceText = relatedProductPrices.nth(i).textContent().trim();
            priceText = priceText.replace("$", "").replace(",", "").trim();
            double relatedPrice = Double.parseDouble(priceText);

            if (relatedPrice < minPrice || relatedPrice > maxPrice) {
                Allure.step("❌ Related product price out of range: ${}"+relatedPrice);
                outOfRange.add(String.valueOf(relatedPrice));
            } else {
                Allure.step("✅ Related product price within range: ${}"+relatedPrice);
            }
        }

        Allure.step("Total related products: {}"+count);
        Allure.step("Related products out of range: {}"+outOfRange.size());
        return outOfRange;
    }
    public boolean isMainProductInRelatedProducts() {
        String mainTitle = productTitle.textContent().trim();
        Allure.step("Main Product Title: {}"+mainTitle);

        int count = relatedProducts.count();
        Allure.step("Total related products: {}"+count);

        for (int i = 0; i < count; i++) {
            String relatedTitle = relatedProducts.nth(i).locator("h3").textContent().trim();
            Allure.step("Related Product " + (i + 1) + " Title: " + relatedTitle);
            if (relatedTitle.equalsIgnoreCase(mainTitle)) {
                Allure.step("❌ Main product found in related products!");
                return true;
            }
        }
        Allure.step("✅ Main product is NOT included in related products.");
        return false;
    }

    public void navigateAndVerifyAllRelatedProducts() {
        int count = relatedProductLinks.count();
        Allure.step("Total related products: {}"+count);

        for (int i = 0; i < count; i++) {
            final int index = i;
            Allure.step("Clicking Related Product #{}"+(index + 1));
            Page relatedTab = page.waitForPopup(() -> relatedProductLinks.nth(index).click());
            EbayProductPage relatedProductPage = new EbayProductPage(relatedTab);
            relatedProductPage.waitForPageLoad();
            relatedProductPage.verifyProductDetailsDisplayed();
            relatedTab.close();
            page.bringToFront();
        }
    }
    public void verifyProductDetailsDisplayed() {
        productTitle.waitFor(new Locator.WaitForOptions().setTimeout(30000));
        org.testng.Assert.assertTrue(productTitle.isVisible(), "Product title is not displayed!");
        Allure.step("✅ Product title is displayed: {}"+productTitle.textContent().trim());

        productPrice.waitFor(new Locator.WaitForOptions().setTimeout(30000));
        org.testng.Assert.assertTrue(productPrice.isVisible(), "Product price is not displayed!");
        Allure.step("✅ Product price is displayed: {}"+productPrice.textContent().trim());
    }
}