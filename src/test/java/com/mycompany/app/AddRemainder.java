package com.mycompany.app;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.Collections;

/**
 * Optimized and simplified test for adding a remainder.
 * Demonstrates clean structure and reusable helper methods.
 */
public class AddRemainder extends BaseTest {
    private LoginPage loginPage;

    // --- Locators ---
    private final By HOME_PLUS_BTN = AppiumBy
            .xpath("//android.widget.ImageView[@content-desc='Home']/preceding-sibling::android.widget.Button[1] | //android.widget.Button[@index='7']");
    private final By TITLE_FIELD = AppiumBy.xpath("//android.widget.EditText[1]");
    private final By CATEGORY_FIELD = AppiumBy
            .xpath("//*[contains(@content-desc, 'Category') or contains(@content-desc, 'Transportation')]");
    private final By CATEGORY_OPTION = AppiumBy.accessibilityId("Car & Transportation");
    private final By DUE_DATE_FIELD = AppiumBy
            .xpath("//*[contains(@content-desc, 'Due Date') or @index='5' or contains(@content-desc, '202')]");
    private final By DONE_BTN = AppiumBy.xpath("//*[@content-desc='Done' or @text='Done']");
    private final By SAVE_BTN = AppiumBy.xpath("//*[@content-desc='Save' or @text='Save']");
    private final By HOME_TAB = AppiumBy.xpath("//*[contains(@content-desc, 'Home')]");

    // --- Target Date/Time ---
    private final String MONTH = "April";
    private final String DAY = "2";
    private final String YEAR = "2026";
    private final String HOUR = "5";
    private final String MINUTE = "36";
    private final String AMPM = "PM";

    @BeforeMethod
    public void setupTest() throws InterruptedException {
        loginPage = new LoginPage(driver);
        // Restored: Ensure login using the robust session-aware logic
        loginPage.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);
    }

    @Test
    public void addRemainderWithDueDate() throws InterruptedException {
        System.out.println("[TEST] Starting: Add Remainder with Due Date");

        openAddReminderScreen();
        enterTitle("My Reminder");
        selectCategory();
        handleDatePicker();
        saveReminder();
        verifyReminderCreated("My Reminder");
    }

    // --- Helper Methods ---

    private void openAddReminderScreen() throws InterruptedException {
        click(HOME_PLUS_BTN);
        Thread.sleep(1500);
    }

    private void enterTitle(String text) {
        WebElement title = waitForVisible(TITLE_FIELD);
        title.click();
        title.clear();
        title.sendKeys(text);
        driver.hideKeyboard();
    }

    private void selectCategory() throws InterruptedException {
        click(CATEGORY_FIELD);
        Thread.sleep(1000);
        click(CATEGORY_OPTION);
        Thread.sleep(1000);
    }

    private void handleDatePicker() throws InterruptedException {
        System.out.println("[INFO] Configuring Date & Time...");
        openDatePicker();

        scrollPicker("month", MONTH);
        scrollPicker("day", DAY);
        scrollPicker("year", YEAR);
        scrollPicker("hour", HOUR);
        scrollPicker("minute", MINUTE);
        scrollPicker("ampm", AMPM);

        click(DONE_BTN);
        Thread.sleep(1500);
    }

    private void openDatePicker() throws InterruptedException {
        boolean opened = false;
        for (int i = 0; i < 3; i++) {
            try {
                WebElement field = waitForVisible(DUE_DATE_FIELD);
                field.click();
                Thread.sleep(1000);

                if (driver.findElements(AppiumBy.className("android.widget.SeekBar")).size() > 0) {
                    opened = true;
                    break;
                }

                clickAtPoint(field, 0.9, 0.5); // Click calendar icon area
                Thread.sleep(1000);
                if (driver.findElements(AppiumBy.className("android.widget.SeekBar")).size() > 0) {
                    opened = true;
                    break;
                }
            } catch (Exception e) {
                System.out.println("[WARN] Retrying date picker open...");
                Thread.sleep(2000);
            }
        }
        Assert.assertTrue(opened, "Failed to open Date Picker");
    }

    private void saveReminder() throws InterruptedException {
        System.out.println("[INFO] Saving Reminder...");
        driver.hideKeyboard();
        Thread.sleep(1000);

        try {
            click(SAVE_BTN);
        } catch (Exception e) {
            System.out.println("[INFO] Save button not visible, scrolling down...");
            performScroll(waitForVisible(By.className("android.widget.ScrollView")), false);
            click(SAVE_BTN);
        }
        Thread.sleep(2000);
    }

    private void verifyReminderCreated(String title) {
        try {
            click(HOME_TAB);
            By createdCard = AppiumBy.xpath("//*[contains(@content-desc, '" + title + "')]");
            waitForPresence(createdCard);
            System.out.println("[SUCCESS] Reminder '" + title + "' verified on Home Screen.");
        } catch (Exception e) {
            Assert.fail("Reminder creation verification failed: " + e.getMessage());
        }
    }

    // --- Utility Methods ---

    private void scrollPicker(String column, String value) throws InterruptedException {
        int index = getPickerIndex(column);
        By locator = AppiumBy.xpath("(//android.widget.SeekBar)[" + index + "]");

        for (int i = 0; i < 15; i++) {
            WebElement entry = driver.findElement(locator);
            String current = entry.getAttribute("content-desc");
            if (current != null && current.toLowerCase().contains(value.toLowerCase())) {
                return;
            }
            performScroll(entry, i < 8);
            Thread.sleep(800);
        }
    }

    private int getPickerIndex(String column) {
        // Standard switch for Java 11 compatibility
        switch (column.toLowerCase()) {
            case "month":
                return 1;
            case "day":
                return 2;
            case "year":
                return 3;
            case "hour":
                return 4;
            case "minute":
                return 5;
            case "ampm":
                return 6;
            default:
                throw new IllegalArgumentException("Invalid picker column: " + column);
        }
    }

    private void click(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    private WebElement waitForVisible(By locator) {
        return new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void waitForPresence(By locator) {
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    private void clickAtPoint(WebElement el, double xPct, double yPct) {
        Rectangle r = el.getRect();
        int x = r.getX() + (int) (r.getWidth() * xPct);
        int y = r.getY() + (int) (r.getHeight() * yPct);

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence tap = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, y))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(tap));
    }

    private void performScroll(WebElement el, boolean up) {
        Rectangle r = el.getRect();
        int x = r.getX() + (r.getWidth() / 2);
        int startY = r.getY() + (int) (r.getHeight() * (up ? 0.7 : 0.3));
        int endY = r.getY() + (int) (r.getHeight() * (up ? 0.3 : 0.7));

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence scroll = new Sequence(finger, 1)
                .addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), x, startY))
                .addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()))
                .addAction(finger.createPointerMove(Duration.ofMillis(600), PointerInput.Origin.viewport(), x, endY))
                .addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        driver.perform(Collections.singletonList(scroll));
    }
}