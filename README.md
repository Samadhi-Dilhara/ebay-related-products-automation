# ebay-related-products-automation
Playwright Java Automation Framework for # eBay Related Best Seller Products — Test Automation

Automated UI test suite for verifying the **Related Best Seller Products** feature on eBay product pages.
Built with **Java + Playwright + TestNG + Allure**.

---

## 📁 Project Structure

```
src/
├── base/
│   └── BaseTest.java                   # Browser setup, teardown, screenshot on failure
│
├── pages/
│   ├── EbayHomePage.java               # eBay home page actions (navigate, search)
│   ├── EbaySearchResultsPage.java      # Search results page actions (click item, pagination)
│   └── EbayProductPage.java            # Product page actions (related products, prices, category)
│
└── tests/
    ├── EbayRelatedProductsTest.java     # TC01–TC06: Related products section tests
    ├── EbayNavigationTest.java          # TC-NAV-003: Navigation tests

TestSuitRunner.xml                              # TestNG suite configuration

🧪 Test Cases

### Related Products Tests — `EbayRelatedProductsTest`

| Test | Description |
|------|-------------|
| TC01 | Verify Related Products section is visible |
| TC02 | Verify at most 6 related products are displayed |
| TC03 | Verify related products belong to same category as main product |
| TC04 | Verify related product prices are within ±20% of main product price |
| TC05 | Verify main product does not appear in related products |
| TC06 | Verify user can navigate to each related product page and see product details |



## Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Google Chrome browser

---

## Setup

**1. Clone the repository**
```bash
git clone https://github.com/samadhi-dilhara/eBay_RelatedBestSellerProductsFeature.git
cd eBay_RelatedBestSellerProductsFeature
```

**2. Install dependencies**
```bash
mvn install
```

**3. Install Playwright browsers**
```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chromium"
```

## Running Tests

**Run all tests:**
```bash
mvn test
```
## 📊 Allure Reports

**Generate and open report after test run:**
```bash
mvn allure:serve
```

**Generate report only (without opening):**
```bash
mvn allure:report
```

Report will be available at `target/site/allure-maven-plugin/index.html`

---

## 🔧 Configuration

All test configuration is managed in `utils/ConfigManager.java`:

| Property | Description |
|----------|-------------|
| `EBAY_HOME_URL` | eBay home page URL |
| `SEARCH_KEYWORD` | Search keyword used across tests (e.g. `wallet`) |
| `TARGET_ITEM_ID` | Specific eBay item ID used for navigation tests |

---

## 📸 Screenshots

Failed tests automatically capture a full-page screenshot saved to the `screenshots/` folder:
```
screenshots/FAIL_TC01_verifyRelatedProductsSectionVisible_1234567890.png
```

---

## 🛠️ Tech Stack

| Tool | Purpose |
|------|---------|
| Java | Programming language |
| Playwright | Browser automation |
| TestNG | Test framework |
| Allure | Test reporting |
| Maven | Build and dependency management |eBay Related Products
