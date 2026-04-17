package com.mycompany.app;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final AndroidDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By landingLoginBtn = AppiumBy.xpath("//*[contains(@content-desc, 'Log in') or contains(@text, 'Log in')]");
    private final By emailField = AppiumBy.xpath("//android.widget.EditText[1]");
    private final By passwordField = AppiumBy.xpath("//android.widget.EditText[2]");
    private final By loginButton = AppiumBy.xpath("//*[@text='Login' or @content-desc='Login' or @index='7']");
    
    // Home Screen Anchors
    private final By homeAnchors = AppiumBy.xpath("//*[contains(@content-desc, 'Hello') or contains(@content-desc, 'Life Admin') or contains(@content-desc, 'Home')]");

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void enterEmail(String email) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(emailField));
        el.click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        el.clear();
        el.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(passwordField));
        el.click();
        try { Thread.sleep(800); } catch (InterruptedException ignored) {}
        el.clear();
        el.sendKeys(password);
    }

    public void tapLogin() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    public void login(String email, String password) throws InterruptedException {
        System.out.println("[INFO] Checking if session is already active...");
        boolean atHome = false;
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            atHome = driver.findElements(homeAnchors).size() > 0;
            if (atHome) {
                System.out.println("[INFO] Session active. Starting test directly...");
                return;
            }
        } catch (Exception ignored) {}

        boolean atLoginOrLanding = false;
        try {
            atLoginOrLanding = driver.findElements(landingLoginBtn).size() > 0 || driver.findElements(emailField).size() > 0;
        } catch (Exception ignored) {}

        if (!atHome && !atLoginOrLanding) {
            System.out.println("[WARNING] App in unknown state. Restarting app...");
            driver.terminateApp("satheia.app.ai");
            Thread.sleep(2000);
            driver.activateApp("satheia.app.ai");
            Thread.sleep(5000);
        }

        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
            if (driver.findElements(landingLoginBtn).size() > 0) {
                driver.findElement(landingLoginBtn).click();
                Thread.sleep(2000);
            }
        } catch (Exception ignored) {
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        }

        enterEmail(email);
        enterPassword(password);

        try { driver.hideKeyboard(); } catch (Exception ignored) {
            driver.navigate().back();
        }
        
        tapLogin();
        
        // Wait until Home screen is actually visible before returning
        System.out.println("[INFO] Waiting for Home screen to load...");
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//*[contains(@content-desc, 'Hello')]")),
            ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//*[contains(@content-desc, 'Life Admin')]")),
            ExpectedConditions.visibilityOfElementLocated(AppiumBy.xpath("//*[contains(@content-desc, 'Home')]"))
        ));
        Thread.sleep(2000); // Small buffer for Flutter UI settling
    }
}
