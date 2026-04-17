# Appium Automation Project

This project contains Android automation tests for the Reminder App using Appium, Java, and TestNG.

## 🚀 Getting Started

### 1. Prerequisites
- **Node.js**: Required to run the Appium Server.
- **Java JDK (11+)**: For compiling and running the tests.
- **Maven**: For dependency management.
- **Android SDK**: Required for ADB and UI Automator 2.
- **Appium Server**: `npm install -g appium` and `appium driver install uiautomator2`.

### 2. Environment Variables
Ensure the following are set in your shell environment:
```bash
export ANDROID_HOME=/path/to/your/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools
```

### 3. Setting Up for a New Project/App
When you receive a new APK or start a new project, modify these core areas:

#### A. App Capabilities (`BaseTest.java`)
Update the `appPackage` and `appActivity` in the `setup()` method to match your new application.
```java
.setAppPackage("com.new.package.name")
.setAppActivity(".MainActivity")
```
*Tip: Use `adb shell dumpsys window | grep -E 'mCurrentFocus'` while the app is open to find these values.*

#### B. Device Configuration
Update the `deviceName` and `platformVersion` if you are using a different physical device or emulator.

#### C. Locators (`Page Files`)
Each app has unique UI elements. Update XPaths/Accessibility IDs in your test classes (e.g., `LoginTest.java`). Prefer **Accessibility IDs** over XPaths for faster and more stable execution.

### 4. Running Tests
1. Start the Appium Server: `appium`
2. Run the tests via Maven:
```bash
mvn test
```

## 📁 Project Structure
- `src/test/java/com/mycompany/app`: Contains test logic.
  - `BaseTest.java`: Setup/Teardown and Appium Driver initialization.
  - `LoginTest.java`: Authentication flows.
  - `AddRemainder.java`: Core feature automation with complex UI interactions (Date Pickers, etc.).
- `apps/`: Store your project APK files here.
- `logs/`: Application and Appium runtime logs.
- `pom.xml`: Project dependencies (Selenium, Appium Java Client, TestNG).

## 💡 Best Practices implemented
- **Explicit Waits**: Used `WebDriverWait` instead of hard coded sleeps where possible.
- **Helper Methods**: Complex gestures (scrolling, clicking icons) are encapsulated in reusable methods.
- **Resilient Locators**: Using contains logic to handle dynamic content descriptions.
