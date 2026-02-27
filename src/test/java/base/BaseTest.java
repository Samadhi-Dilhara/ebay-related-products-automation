package base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class BaseTest {
    protected Playwright playwright;
    protected Browser browser;
    protected Page page;

    @BeforeMethod
    public void setup() throws IOException {
        Files.createDirectories(Paths.get("screenshots"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(0)
        );
        page = browser.newPage(
                new Browser.NewPageOptions()
                        .setViewportSize(1280, 800)
        );
        page.setDefaultTimeout(15000);
        page.setDefaultNavigationTimeout(30000);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        try {
            // Always wrap in Allure step for reporting
            Allure.step("Tearing down test: " + result.getName(), () -> {
                if (result.getStatus() == ITestResult.FAILURE) {
                    // Take screenshot
                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));

                    // Attach screenshot to Allure report
                    Allure.addAttachment("Screenshot - " + result.getName(),
                            new ByteArrayInputStream(screenshot));

                    // Add step describing failure
                    Allure.step("❌ Test FAILED: " + result.getName(), () -> {
                        Allure.step("Error message: " + result.getThrowable().getMessage());
                    });
                } else if (result.getStatus() == ITestResult.SUCCESS) {
                    Allure.step("✅ Test PASSED: " + result.getName());
                } else if (result.getStatus() == ITestResult.SKIP) {
                    Allure.step("⚠ Test SKIPPED: " + result.getName());
                }

                // Close browser and Playwright
                if (browser != null) browser.close();
                if (playwright != null) playwright.close();
            });
        } catch (Exception e) {
            Allure.step("⚠ TearDown failed: " + e.getMessage());
        }
    }
}