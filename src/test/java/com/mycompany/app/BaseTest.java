package com.mycompany.app;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.ITestResult;
import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base configuration class for all Android tests.
 * Handles Appium driver setup and teardown.
 */
public class BaseTest {
    protected AndroidDriver driver;

    @BeforeClass
    public void setup() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setDeviceName(System.getenv().getOrDefault("ANDROID_DEVICE_NAME", "R9ZY20S3CJA"))
                .setPlatformVersion(System.getenv().getOrDefault("ANDROID_PLATFORM_VERSION", "16"))
                .setAutomationName("UiAutomator2")
                .setAppPackage("satheia.app.ai")
                .setAppActivity(".MainActivity")
                .setNoReset(true)
                .setAutoGrantPermissions(true)
                .setAppWaitDuration(Duration.ofMinutes(1));

        String appiumUrl = System.getenv().getOrDefault("APPIUM_SERVER_URL", "http://127.0.0.1:4723");
        
        System.out.println("[INFO] Connecting to Appium at: " + appiumUrl);
        driver = new AndroidDriver(URI.create(appiumUrl).toURL(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        System.out.println("[INFO] Session established successfully.");
    }

    /**
     * Capture a screenshot of the current device screen and save to 'screenshots/' folder.
     */
    public String captureScreenshot(String testName) {
        if (driver == null) return null;
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS"));
            String fileName = testName + "_" + timestamp + ".png";
            File screenshotDir = new File("fail-reports"); // Stay out of target/ so it's not deleted
            if (!screenshotDir.exists()) {
                boolean created = screenshotDir.mkdirs();
                if (!created) System.err.println("[ERROR] Could not create fail-reports directory");
            }

            File sourceFile = ((org.openqa.selenium.TakesScreenshot) driver).getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            File destinationFile = new File(screenshotDir, fileName);
            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            
            String absolutePath = destinationFile.getAbsolutePath();
            System.out.println("[INFO] Screenshot captured: " + absolutePath);
            return absolutePath;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    @AfterMethod
    public void screenshotOnFailure(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            System.err.println("[FAIL] Test '" + result.getName() + "' failed. Capturing screenshot...");
            captureScreenshot(result.getName() + "_Failure");
        }
    }

    @AfterClass
    public void teardown() {
        if (driver != null) {
            driver.quit();
            System.out.println("[INFO] Session closed.");
        }
    }
}
