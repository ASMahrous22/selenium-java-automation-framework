# ASM Selenium Framework

A lightweight Java wrapper around Selenium WebDriver that provides a clean, readable API for browser automation — built to reduce boilerplate and make test scripts easier to write and maintain.

---

## Features

- **Multi-browser support** — Chrome, Firefox, Edge, Safari
- **Configurable browser options** — headless, kiosk, maximized, custom profiles, and arbitrary arguments via a fluent `BrowserOptions` builder
- **Smart element finding** — supports ID, Name, Class, XPath, CSS Selector
- **Dual input style** — every method accepts both `By` locator and `WebElement`
- **Built-in Explicit & Fluent Waits** — clickable, visibility, and presence checks before every interaction
- **Flexible Implicit Wait** — set wait duration using human-readable strings like `"seconds"` or `"ms"`
- **Actions support** — right-click, double-click, hover, drag & drop, scroll-to-center
- **Dropdown handling** — select/deselect by index, value, visible text, or partial text
- **Checkbox & Radio support** — smart check/uncheck with state awareness
- **Element state validation** — check if elements are visible, enabled, or selected
- **Multiple window handling** — switch between tabs/windows by handle, index, or auto-detect new
- **Alert handling** — accept, dismiss, read, and type into JavaScript dialogs
- **IFrame handling** — switch into/out of frames by locator, element, index, or name/id
- **Timestamped screenshots** — capture and auto-save PNGs to a `Screenshots/` folder
- **Defensive null checks** — clear error messages when elements are missing

---

## Tech Stack

| Tool | Version |
|------|---------|
| Java | 11+ |
| Selenium WebDriver | 4.x |
| Maven | 3.x |

---

## Project Structure

```
selenium_java_automation_framework/
├── src/
│   └── main/
│       └── java/
│           └── utils/
│               ├── ASM_Framework.java          ← Single entry point for all test scripts
│               └── framework/
│                   ├── BrowserManager.java     ← Browser launch, navigation, window size
│                   ├── WaitManager.java        ← Implicit, explicit, fluent waits
│                   ├── ElementFinder.java      ← Locating elements in the DOM
│                   ├── ElementInteractions.java← Click, type, clear, getText, state checks
│                   ├── ActionsManager.java     ← Hover, right-click, double-click, drag, scroll
│                   ├── DropdownManager.java    ← Select / deselect from <select>
│                   ├── WindowManager.java      ← Tab / window switching
│                   ├── AlertManager.java       ← JavaScript alert / confirm / prompt
│                   ├── FrameManager.java       ← IFrame context switching
│                   └── ScreenshotManager.java  ← Timestamped PNG capture
├── src/
│   └── test/
│       └── java/                               ← Your test classes go here
├── Screenshots/                                ← Auto-created; stores captured screenshots
├── pom.xml
└── README.md
```

---

## Getting Started

### 1. Clone the repo
```bash
git clone https://github.com/ASMahrous22/selenium-java-automation-framework.git
cd selenium-java-automation-framework
```

### 2. Add Selenium to `pom.xml`
```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.40.0</version>
</dependency>
```

### 3. Run your first test
```java
ASM_Framework driver = new ASM_Framework("chrome");
driver.goToURL("https://example.com");
driver.manageScreenSize("maximize");

WebElement searchBox = driver.findElement("id", "search");
driver.writeInElement(searchBox, "Selenium");

driver.closeAllTabs();
```

---

## API Reference

### Browser Control

| Method | Description |
|--------|-------------|
| `goToURL(url)` | Navigate to a URL |
| `getCurrentPageTitle()` | Get the page title |
| `getCurrentPageURL()` | Get the current URL |
| `manageNavigationButtons("back/forward/refresh")` | Browser navigation |
| `manageScreenSize("maximize/minimize")` | Window size control |
| `closeCurrentTab()` | Close the active tab |
| `closeAllTabs()` | Quit the browser session |

#### Browser Options

Use the fluent `BrowserOptions` builder for custom launch configuration. Only Chrome, Firefox, and Edge support options; Safari uses default config.

```java
ASM_Framework driver = new ASM_Framework("chrome",
    new ASM_Framework.BrowserOptions()
        .headless()
        .maximized()
        .withArgument("--disable-notifications")
);
```

| Method | Description |
|--------|-------------|
| `.headless()` | Run with no visible UI — ideal for CI environments |
| `.kiosk()` | Launch in full-screen borderless kiosk mode (takes precedence over headless) |
| `.maximized()` | Launch in a maximized window |
| `.withUserDataDir(path)` | Load a specific browser profile by absolute path |
| `.withArgument(arg)` | Append an arbitrary browser argument (e.g., `"--disable-infobars"`) |

---

### Element Finding

| Method | Description |
|--------|-------------|
| `findElement(by, locator)` | Find element using default timeout — returns `WebElement` |
| `findElement(by, locator, timeoutSeconds)` | Find element with a custom timeout — returns `WebElement` |
| `getBy(by, locator)` | Convert string strategy to `By` object |

**Supported locator strategies:** `"id"`, `"name"`, `"class"`, `"xpath"`, `"css"`

---

### Element Interaction

All interaction methods accept both `By` and `WebElement`.

| Method | Description |
|--------|-------------|
| `clickElement(By / WebElement)` | Wait until clickable, then click |
| `doubleClick(By / WebElement)` | Double-click an element |
| `writeInElement(By / WebElement, text)` | Clear field and type text |
| `clearElementText(By / WebElement)` | Clear an input field |
| `getElementText(By / WebElement)` | Get visible text of an element |

---

### Actions

| Method | Description |
|--------|-------------|
| `rightClick(By)` | Right-click (context menu) on an element |
| `doubleClick(By / WebElement)` | Double-click an element |
| `hoverOverElement(By / WebElement)` | Hover mouse over an element |
| `dragAndDrop(By source, By target)` | Drag source and drop onto target |
| `scrollToElement(By / WebElement)` | Scroll element to center of viewport |

---

### Checkbox & Radio

| Method | Description |
|--------|-------------|
| `checkCheckbox(By)` | Check a checkbox if not already checked |
| `uncheckCheckbox(By)` | Uncheck a checkbox if currently checked |
| `selectRadioButton(By)` | Select a radio button if not already selected |

---

### Dropdown Handling

All dropdown methods accept both `By` and `WebElement`.

| Method | Description |
|--------|-------------|
| `selectFromDropDownMenu(By / WebElement, by, value)` | Select a dropdown option |
| `deselectFromDropDownMenu(By / WebElement, by, value)` | Deselect a dropdown option |

**Selection strategies:** `"index"`, `"value"`, `"visible text"`, `"contains text"`, `"all"` *(deselect only)*

---

### Element State Validation

| Method | Returns | Description |
|--------|---------|-------------|
| `validateElementIsDisplayed(element)` | `boolean` | Is the element visible? |
| `validateElementIsEnabled(element)` | `boolean` | Is the element interactable? |
| `validateElementIsSelected(element)` | `boolean` | Is the element selected? |

---

### Wait Management

| Method | Description |
|--------|-------------|
| `setImplicitWait(Duration)` | Set implicit wait using a `Duration` object |
| `setImplicitWait(long seconds)` | Set implicit wait using a plain seconds value |
| `setImplicitWait(String unit, long time)` | Set implicit wait using a readable unit string |
| `setExplicitWait(By, long timeoutSeconds)` | Wait for element presence with a custom timeout |
| `setExplicitWait(By, Duration timeout)` | Wait for element presence with a `Duration` timeout |
| `setFluentWait(By, long timeout, long pollingMs, String message)` | Fluent wait with polling interval and custom message |
| `setFluentWait(By, Duration timeout, Duration polling, String message)` | Fluent wait using `Duration` overloads |

**Supported time units for implicit wait (case-insensitive):**
`"seconds"` / `"sec"`, `"minutes"` / `"min"`, `"hours"` / `"hour"`, `"days"` / `"day"`, `"ms"` / `"mili"`, `"ns"` / `"nano"`

> ⚠️ **Warning:** Avoid mixing Implicit and Explicit Waits — this can cause unpredictable timeout behavior in Selenium.

---

### Window Handling

| Method | Description |
|--------|-------------|
| `getCurrentWindowHandle()` | Returns the handle of the currently focused window/tab |
| `getAllWindowHandles()` | Returns all open window handles as an ordered `List` |
| `switchToWindowByHandle(handle)` | Switch focus to a window by its handle string |
| `switchToWindowByIndex(index)` | Switch focus to a window by its 0-based index |
| `switchToNewWindow(parentHandle)` | Auto-detect and switch to the newest window/tab |
| `closeCurrentWindowAndSwitchTo(handle)` | Close current window and return focus to a given handle |
| `getWindowCount()` | Returns the total number of open windows/tabs |

**Usage example:**
```java
String mainWindow = driver.getCurrentWindowHandle();
driver.clickElement(By.linkText("Open in new tab"));
driver.switchToNewWindow(mainWindow);
// ... interact with the new tab ...
driver.closeCurrentWindowAndSwitchTo(mainWindow);
```

---

### Alert Handling

| Method | Description |
|--------|-------------|
| `acceptAlert()` | Click OK on an alert, confirm, or prompt |
| `dismissAlert()` | Click Cancel on a confirm or prompt |
| `getAlertText()` | Read the message text of the current alert |
| `typeInAlert(text)` | Type into a prompt dialog then accept it |

All alert methods automatically wait up to `DEFAULT_TIMEOUT` (10 seconds) for the dialog to appear.

**Usage examples:**
```java
driver.clickElement(By.id("deleteBtn"));
System.out.println(driver.getAlertText()); // "Are you sure?"
driver.acceptAlert();
```

```java
driver.clickElement(By.id("promptBtn"));
driver.typeInAlert("John Doe"); // types and clicks OK
```

---

### IFrame Handling

| Method | Description |
|--------|-------------|
| `switchToIFrame(By)` | Switch into an iframe using a `By` locator |
| `switchToIFrame(WebElement)` | Switch into an iframe using a `WebElement` reference |
| `switchToIFrameByIndex(index)` | Switch into an iframe by its 0-based position on the page |
| `switchToIFrameByNameOrId(nameOrId)` | Switch into an iframe by its `name` or `id` attribute |
| `switchToDefaultContent()` | Return to the top-level page content |
| `switchToParentFrame()` | Step one level up from a nested iframe |

**Usage example:**
```java
driver.switchToIFrame(By.id("paymentFrame"));
driver.writeInElement(By.id("cardNumber"), "4111111111111111");
driver.switchToDefaultContent(); // back to the main page
```

---

### Screenshots

| Method | Description |
|--------|-------------|
| `takeScreenshot(label)` | Capture the current page as a PNG and save it to `Screenshots/` |

Screenshots are saved to a `Screenshots/` folder at the project root. The folder is created automatically if it does not exist. Each file is named using your label plus a full timestamp to prevent overwrites:

```
Screenshots/
├── LoginPage_2025-07-21_14-35-22-123.png
├── AfterSubmit_2025-07-21_14-35-45-456.png
└── ErrorState_2025-07-21_14-36-01-789.png
```

**Usage example:**
```java
driver.goToURL("https://example.com/login");
driver.takeScreenshot("LoginPage");

driver.writeInElement(By.id("username"), "admin");
driver.clickElement(By.id("submit"));
driver.takeScreenshot("AfterSubmit");
```

---

## Full Usage Example

```java
// Initialize with options
ASM_Framework driver = new ASM_Framework("chrome",
    new ASM_Framework.BrowserOptions().maximized()
);
driver.goToURL("https://the-internet.herokuapp.com/login");
driver.takeScreenshot("LoginPage");

// Login
driver.writeInElement(By.id("username"), "tomsmith");
driver.writeInElement(By.id("password"), "SuperSecretPassword!");
driver.clickElement(By.xpath("//button[@type='submit']"));
driver.takeScreenshot("AfterLogin");

// Validate result
System.out.println(driver.getElementText(By.cssSelector(".flash.success")));

// Dropdown
driver.selectFromDropDownMenu(By.id("dropdown"), "index", "1");

// Checkbox
driver.checkCheckbox(By.id("agreeTerms"));

// Hover then click
driver.hoverOverElement(By.id("menu"));
driver.clickElement(By.id("menuItem"));

// Handle a new tab
String mainWindow = driver.getCurrentWindowHandle();
driver.clickElement(By.linkText("Open new tab"));
driver.switchToNewWindow(mainWindow);
driver.takeScreenshot("NewTab");
driver.closeCurrentWindowAndSwitchTo(mainWindow);

// Handle an alert
driver.clickElement(By.id("triggerAlert"));
System.out.println(driver.getAlertText());
driver.acceptAlert();

// Work inside an iframe
driver.switchToIFrame(By.id("contentFrame"));
driver.writeInElement(By.id("innerInput"), "Hello from iframe");
driver.switchToDefaultContent();

driver.closeAllTabs();
```

---

## Author

**ASMahrous** — Built as part of a Selenium automation learning journey.

Feel free to fork, use, or contribute!
