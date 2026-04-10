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

## Manager Reference

The framework delegates every responsibility to a dedicated manager class. Each section below covers the manager's methods and shows how to use it standalone — without going through `ASM_Framework` — if you ever need direct access.

---

### BrowserManager

Handles browser lifecycle, navigation, and window size.

**Methods:**

| Method | Description |
|--------|-------------|
| `getDriver()` | Returns the underlying `WebDriver` instance |
| `goToURL(url)` | Navigate to a URL |
| `getCurrentPageTitle()` | Get the page title |
| `getCurrentPageURL()` | Get the current URL |
| `manageNavigationButtons("back/forward/refresh")` | Simulate browser navigation buttons |
| `manageScreenSize("maximize/minimize/fullscreen")` | Control window size |
| `closeCurrentTab()` | Close the active tab |
| `closeAllTabs()` | Quit the entire browser session |

**BrowserOptions builder:**

| Method | Description |
|--------|-------------|
| `.headless()` | Run with no visible UI — ideal for CI environments |
| `.kiosk()` | Full-screen borderless kiosk mode (takes precedence over headless) |
| `.maximized()` | Launch in a maximized window |
| `.withUserDataDir(path)` | Load a specific browser profile by absolute path |
| `.withArgument(arg)` | Append an arbitrary browser argument (e.g., `"--disable-infobars"`) |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome",
    new BrowserManager.BrowserOptions()
        .headless()
        .maximized()
        .withArgument("--disable-notifications")
);

WebDriver driver = browser.getDriver();

browser.goToURL("https://example.com");
System.out.println(browser.getCurrentPageTitle());
System.out.println(browser.getCurrentPageURL());

browser.manageNavigationButtons("back");
browser.manageNavigationButtons("forward");
browser.manageNavigationButtons("refresh");

browser.manageScreenSize("maximize");
browser.closeCurrentTab();
browser.closeAllTabs();
```

---

### WaitManager

Centralizes all Selenium waiting strategies — implicit, explicit, and fluent — plus internal helpers used across all other managers.

**Public methods:**

| Method | Description |
|--------|-------------|
| `setImplicitWait(Duration)` | Set implicit wait using a `Duration` object |
| `setImplicitWait(long seconds)` | Set implicit wait using a plain seconds value |
| `setImplicitWait(String unit, long time)` | Set implicit wait using a readable unit string |
| `setExplicitWait(By, long timeoutSeconds)` | Wait for element presence with a custom timeout in seconds |
| `setExplicitWait(By, Duration timeout)` | Wait for element presence with a `Duration` timeout |
| `setFluentWait(By, long timeout, long pollingMs, String message)` | Fluent wait with seconds + polling interval |
| `setFluentWait(By, Duration timeout, Duration polling, String message)` | Fluent wait using `Duration` overloads |

**Internal helpers (available for direct use):**

| Method | Returns | Description |
|--------|---------|-------------|
| `waitForElementToBeClickable(By)` | `WebElement` | Waits using default timeout |
| `waitForElementToBeClickable(By, long seconds)` | `WebElement` | Waits using custom timeout |
| `waitForElementToBeClickable(WebElement)` | `WebElement` | Waits using default timeout |
| `waitForElementToBeClickable(WebElement, long seconds)` | `WebElement` | Waits using custom timeout |
| `waitForElementToBeVisible(By)` | `WebElement` | Waits using default timeout |
| `waitForElementToBeVisible(By, long seconds)` | `WebElement` | Waits using custom timeout |
| `waitForElementToBeVisible(WebElement)` | `WebElement` | Waits using default timeout |
| `waitForElementToBeVisible(WebElement, long seconds)` | `WebElement` | Waits using custom timeout |
| `waitForElementToBePresent(By)` | `WebElement` | Waits using default timeout |
| `waitForElementToBePresent(By, Duration)` | `WebElement` | Waits using `Duration` timeout |
| `waitForElementToBePresent(By, long seconds)` | `WebElement` | Waits using custom timeout |
| `waitForAlert()` | `Alert` | Waits for a JS dialog using default timeout |
| `waitForAlert(long seconds)` | `Alert` | Waits for a JS dialog using custom timeout |

**Supported time units for `setImplicitWait(String, long)` (case-insensitive):**
`"seconds"` / `"sec"`, `"minutes"` / `"min"`, `"hours"` / `"hour"`, `"days"` / `"day"`, `"ms"` / `"mili"`, `"ns"` / `"nano"`

> ⚠️ **Warning:** Avoid mixing Implicit and Explicit Waits — this can cause unpredictable timeout behavior in Selenium.

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));

// Implicit wait
wait.setImplicitWait(Duration.ofSeconds(5));
wait.setImplicitWait(5);
wait.setImplicitWait("seconds", 5);
wait.setImplicitWait("ms", 500);

// Explicit wait — waits for the element to appear in the DOM
wait.setExplicitWait(By.id("loginBtn"), 10);
wait.setExplicitWait(By.id("loginBtn"), Duration.ofSeconds(10));

// Fluent wait — polls every 500ms, throws with a custom message on timeout
wait.setFluentWait(By.id("spinner"), 15, 500, "Spinner never disappeared!");
wait.setFluentWait(
    By.id("spinner"),
    Duration.ofSeconds(15),
    Duration.ofMillis(500),
    "Spinner never disappeared!"
);

// Internal helpers — used by other managers but available directly
WebElement btn = wait.waitForElementToBeClickable(By.id("submit"));
WebElement msg = wait.waitForElementToBeVisible(By.cssSelector(".alert"), 5);
Alert alert   = wait.waitForAlert();

browser.closeAllTabs();
```

---

### ElementFinder

Converts human-readable locator strategy strings into `By` objects, and locates elements using `WaitManager`.

**Methods:**

| Method | Description |
|--------|-------------|
| `getBy(locatorType, locator)` | Convert a strategy string to a Selenium `By` object |
| `findElement(locatorType, locator)` | Wait for element presence using default timeout — returns `WebElement` |
| `findElement(locatorType, locator, timeoutSeconds)` | Wait for element presence with a custom timeout — returns `WebElement` |

**Supported locator strategies:** `"id"`, `"name"`, `"class"` / `"class name"`, `"xpath"`, `"css"` / `"css selector"`

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://example.com");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementFinder finder = new ElementFinder(wait);

// Convert a strategy string to a By object
By byId  = finder.getBy("id", "username");
By byXp  = finder.getBy("xpath", "//button[@type='submit']");
By byCss = finder.getBy("css", ".nav-link.active");

// Find an element (waits for DOM presence)
WebElement searchBox = finder.findElement("id", "search");
WebElement header    = finder.findElement("xpath", "//h1");

// Find with a custom timeout
WebElement slowWidget = finder.findElement("css", ".lazy-loaded", 20);

browser.closeAllTabs();
```

---

### ElementInteractions

Handles all direct interactions with `WebElement`s — clicking, typing, clearing, reading text, and validating state. Every method waits for the element to be in the correct state before acting.

**Methods:**

| Method | Description |
|--------|-------------|
| `clickElement(By)` | Wait until clickable, then click |
| `clickElement(WebElement)` | Wait until clickable, then click |
| `writeInElement(By, text)` | Wait for visibility, clear field, then type |
| `writeInElement(WebElement, text)` | Clear field, then type |
| `clearElementText(By)` | Wait for visibility, then clear the field |
| `clearElementText(WebElement)` | Clear the field |
| `getElementText(By)` | Wait for visibility, return visible text |
| `getElementText(WebElement)` | Return visible text |
| `validateElementIsDisplayed(WebElement)` | Returns `true` if the element is visible |
| `validateElementIsEnabled(WebElement)` | Returns `true` if the element is interactable |
| `validateElementIsSelected(WebElement)` | Returns `true` if the element is selected |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/login");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementFinder finder = new ElementFinder(wait);
ElementInteractions interact = new ElementInteractions(wait);

// Click by locator
interact.clickElement(By.id("loginBtn"));

// Type into a field by locator
interact.writeInElement(By.id("username"), "tomsmith");
interact.writeInElement(By.id("password"), "SuperSecretPassword!");

// Click the submit button
interact.clickElement(By.xpath("//button[@type='submit']"));

// Read the result message
String message = interact.getElementText(By.cssSelector(".flash.success"));
System.out.println(message);

// Use a WebElement reference instead
WebElement logoutBtn = finder.findElement("css", ".icon-signout");
System.out.println(interact.validateElementIsDisplayed(logoutBtn)); // true
System.out.println(interact.validateElementIsEnabled(logoutBtn));   // true
interact.clickElement(logoutBtn);

browser.closeAllTabs();
```

---

### ActionsManager

Wraps Selenium's `Actions` API for advanced user-gesture interactions: hover, right-click, double-click, drag-and-drop, scroll, checkbox, and radio operations.

**Methods:**

| Method | Description |
|--------|-------------|
| `rightClick(By)` | Right-click (context menu) on an element |
| `doubleClick(By)` | Double-click using a `By` locator |
| `doubleClick(WebElement)` | Double-click using a `WebElement` reference |
| `hoverOverElement(By)` | Hover mouse over an element by locator |
| `hoverOverElement(WebElement)` | Hover mouse over a `WebElement` |
| `dragAndDrop(By source, By target)` | Drag source element and drop onto target |
| `scrollToElement(By)` | Scroll element to center of viewport by locator |
| `scrollToElement(WebElement)` | Scroll element to center of viewport |
| `checkCheckbox(By)` | Check a checkbox if not already checked |
| `uncheckCheckbox(By)` | Uncheck a checkbox if currently checked |
| `selectRadioButton(By)` | Select a radio button if not already selected |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ActionsManager actions = new ActionsManager(driver, wait);

// Hover over a menu to reveal sub-items
actions.hoverOverElement(By.id("menu"));

// Right-click to open a context menu
actions.rightClick(By.id("hot-spot"));

// Double-click an element
actions.doubleClick(By.id("double-click-btn"));

// Drag and drop
actions.dragAndDrop(By.id("column-a"), By.id("column-b"));

// Scroll a lazy-loaded element into view
actions.scrollToElement(By.id("footer-section"));

// Checkbox with state awareness — won't double-click if already checked
actions.checkCheckbox(By.id("agreeTerms"));
actions.uncheckCheckbox(By.id("agreeTerms"));

// Radio button — only clicks if not already selected
actions.selectRadioButton(By.id("optionB"));

browser.closeAllTabs();
```

---

### DropdownManager

Handles HTML `<select>` dropdown interactions — selecting and deselecting options by index, value, exact text, or partial text.

**Methods:**

| Method | Description |
|--------|-------------|
| `selectFromDropDownMenu(By, selectBy, value)` | Select an option using a `By` locator |
| `selectFromDropDownMenu(WebElement, selectBy, value)` | Select an option using a `WebElement` |
| `deselectFromDropDownMenu(By, deselectBy, value)` | Deselect an option using a `By` locator |
| `deselectFromDropDownMenu(WebElement, deselectBy, value)` | Deselect an option using a `WebElement` |

**Selection strategies (case-insensitive):**

| Strategy | Description |
|----------|-------------|
| `"index"` | 0-based option position |
| `"value"` | Option `value` attribute |
| `"visible text"` / `"visible"` | Exact visible text |
| `"contains text"` / `"contains"` | Partial visible text |
| `"all"` | Deselect every selected option *(deselect only)* |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/dropdown");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
DropdownManager dropdown = new DropdownManager(wait);

// Select by index (0-based)
dropdown.selectFromDropDownMenu(By.id("dropdown"), "index", "1");

// Select by value attribute
dropdown.selectFromDropDownMenu(By.id("dropdown"), "value", "2");

// Select by exact visible text
dropdown.selectFromDropDownMenu(By.id("dropdown"), "visible text", "Option 2");

// Select by partial visible text
dropdown.selectFromDropDownMenu(By.id("dropdown"), "contains text", "Option");

// Using a WebElement reference
ElementFinder finder = new ElementFinder(wait);
WebElement dd = finder.findElement("id", "dropdown");
dropdown.selectFromDropDownMenu(dd, "index", "2");

// Deselect (multi-select dropdowns only)
dropdown.deselectFromDropDownMenu(By.id("multi"), "value", "volvo");
dropdown.deselectFromDropDownMenu(By.id("multi"), "all", "");

browser.closeAllTabs();
```

---

### WindowManager

Manages browser windows and tabs — switching by handle, index, or auto-detecting new ones, as well as closing windows.

**Methods:**

| Method | Description |
|--------|-------------|
| `getCurrentWindowHandle()` | Returns the handle of the currently focused window/tab |
| `getAllWindowHandles()` | Returns all open window handles as an ordered `List` |
| `getWindowCount()` | Returns the total number of open windows/tabs |
| `switchToWindowByHandle(handle)` | Switch focus to a window by its handle string |
| `switchToWindowByIndex(index)` | Switch focus to a window by its 0-based index |
| `switchToNewWindow(parentHandle)` | Auto-detect and switch to the newest window/tab |
| `closeCurrentWindowAndSwitchTo(handle)` | Close current window and return focus to a given handle |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/windows");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementInteractions interact = new ElementInteractions(wait);
WindowManager windows = new WindowManager(driver);

// Store the main window handle before opening anything new
String mainWindow = windows.getCurrentWindowHandle();
System.out.println("Open windows: " + windows.getWindowCount()); // 1

// Click a link that opens a new tab
interact.clickElement(By.linkText("Click Here"));

// Auto-switch to the newly opened tab
windows.switchToNewWindow(mainWindow);
System.out.println("Open windows: " + windows.getWindowCount()); // 2

// Switch back to the main window by handle
windows.switchToWindowByHandle(mainWindow);

// Or switch by index (0 = first opened)
windows.switchToWindowByIndex(0);

// List all handles
List<String> handles = windows.getAllWindowHandles();
System.out.println(handles);

// Close the new window and return to main
windows.switchToNewWindow(mainWindow);
windows.closeCurrentWindowAndSwitchTo(mainWindow);
System.out.println("Open windows: " + windows.getWindowCount()); // 1

browser.closeAllTabs();
```

---

### AlertManager

Handles JavaScript `alert`, `confirm`, and `prompt` dialogs. All methods automatically wait for the dialog to appear before acting.

**Methods:**

| Method | Description |
|--------|-------------|
| `acceptAlert()` | Click OK on an alert, confirm, or prompt |
| `dismissAlert()` | Click Cancel on a confirm or prompt |
| `getAlertText()` | Read the message text of the current alert |
| `typeInAlert(text)` | Type into a prompt dialog then accept it |

All methods wait up to `DEFAULT_TIMEOUT` (10 seconds) for the dialog to appear.

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/javascript_alerts");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementInteractions interact = new ElementInteractions(wait);
AlertManager alerts = new AlertManager(wait);

// Simple alert — read message then accept
interact.clickElement(By.xpath("//button[text()='Click for JS Alert']"));
System.out.println(alerts.getAlertText()); // "I am a JS Alert"
alerts.acceptAlert();

// Confirm dialog — accept or dismiss
interact.clickElement(By.xpath("//button[text()='Click for JS Confirm']"));
System.out.println(alerts.getAlertText()); // "I am a JS Confirm"
alerts.dismissAlert(); // clicks Cancel

// Prompt dialog — type text then accept
interact.clickElement(By.xpath("//button[text()='Click for JS Prompt']"));
alerts.typeInAlert("Hello from automation!"); // types and clicks OK

browser.closeAllTabs();
```

---

### FrameManager

Handles switching between iframes and the main page context. Supports switching by `By` locator, `WebElement`, index, or `name`/`id` attribute.

**Methods:**

| Method | Description |
|--------|-------------|
| `switchToIFrame(By)` | Switch into an iframe using a `By` locator |
| `switchToIFrame(WebElement)` | Switch into an iframe using a `WebElement` reference |
| `switchToIFrameByIndex(index)` | Switch into an iframe by its 0-based position on the page |
| `switchToIFrameByNameOrId(nameOrId)` | Switch into an iframe by its `name` or `id` attribute |
| `switchToDefaultContent()` | Return to the top-level page content |
| `switchToParentFrame()` | Step one level up from a nested iframe |

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://the-internet.herokuapp.com/iframe");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementInteractions interact = new ElementInteractions(wait);
FrameManager frames = new FrameManager(driver, wait);

// Switch by By locator
frames.switchToIFrame(By.id("mce_0_ifr"));
interact.writeInElement(By.id("tinymce"), "Hello from inside the iframe!");
frames.switchToDefaultContent(); // back to the main page

// Switch by name or id attribute
frames.switchToIFrameByNameOrId("mce_0_ifr");
frames.switchToDefaultContent();

// Switch by 0-based index (first iframe on the page)
frames.switchToIFrameByIndex(0);
frames.switchToDefaultContent();

// Nested iframes — step up one level at a time
frames.switchToIFrameByIndex(0);    // enter outer frame
frames.switchToIFrameByIndex(0);    // enter inner frame
frames.switchToParentFrame();       // back to outer frame
frames.switchToDefaultContent();    // back to main page

browser.closeAllTabs();
```

---

### ScreenshotManager

Captures the current browser state as a PNG and saves it with a label + timestamp to prevent overwrites.

**Methods:**

| Method | Description |
|--------|-------------|
| `takeScreenshot(label)` | Capture the current page and save to `Screenshots/` |

Screenshots are saved to a `Screenshots/` folder at the project root. The folder is created automatically if it does not exist. Spaces in the label are replaced with underscores.

```
Screenshots/
├── LoginPage_2025-07-21_14-35-22-123.png
├── AfterSubmit_2025-07-21_14-35-45-456.png
└── ErrorState_2025-07-21_14-36-01-789.png
```

**Standalone usage:**
```java
BrowserManager browser = new BrowserManager("chrome");
WebDriver driver = browser.getDriver();
browser.goToURL("https://example.com/login");

WaitManager wait = new WaitManager(driver, Duration.ofSeconds(10));
ElementInteractions interact = new ElementInteractions(wait);
ScreenshotManager screenshots = new ScreenshotManager(driver);

// Capture the initial page state
screenshots.takeScreenshot("LoginPage");

// Fill in credentials and submit
interact.writeInElement(By.id("username"), "admin");
interact.writeInElement(By.id("password"), "secret");
interact.clickElement(By.id("submit"));

// Capture the result
screenshots.takeScreenshot("AfterLogin");

// Capture on failure — label clearly for easy debugging
screenshots.takeScreenshot("Error_InvalidCredentials");

browser.closeAllTabs();
```

---

## Full Usage Example (via ASM_Framework)

The `ASM_Framework` class wires all managers together into a single entry point. Use it for test scripts instead of instantiating managers directly.

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
