# ASM Selenium Framework

A lightweight Java wrapper around Selenium WebDriver that provides a clean, readable API for browser automation — built to reduce boilerplate and make test scripts easier to write and maintain.

---

## Features

- **Multi-browser support** — Chrome, Firefox, Edge, Safari
- **Smart element finding** — supports ID, Name, Class, XPath, CSS Selector
- **Dual input style** — every method accepts both `By` locator and `WebElement`
- **Built-in Explicit & Fluent Waits** — clickable and visibility checks before every interaction
- **Flexible Implicit Wait** — set wait duration using human-readable strings like `"seconds"` or `"ms"`
- **Actions support** — right-click, double-click, hover, drag & drop, scroll
- **Dropdown handling** — select/deselect by index, value, visible text, or partial text
- **Checkbox & Radio support** — smart check/uncheck with state awareness
- **Element state validation** — check if elements are visible, enabled, or selected
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
│               └── ASM_Framework.java   ← The framework wrapper
├── src/
│   └── test/
│       └── java/                        ← Your test classes go here
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
    <version>4.18.1</version>
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

### Element Finding

| Method | Description |
|--------|-------------|
| `findElement(by, locator)` | Find element — returns `WebElement` |
| `getBy(by, locator)` | Convert string strategy to `By` object |

**Supported locator strategies:** `"id"`, `"name"`, `"class"`, `"xpath"`, `"css"`

### Element Interaction

All interaction methods accept both `By` and `WebElement`.

| Method | Description |
|--------|-------------|
| `clickElement(By / WebElement)` | Wait until clickable, then click |
| `doubleClick(By / WebElement)` | Double-click an element |
| `writeInElement(By / WebElement, text)` | Clear field and type text |
| `clearElementText(By / WebElement)` | Clear an input field |
| `getElementText(By / WebElement)` | Get visible text of an element |

### Actions

| Method | Description |
|--------|-------------|
| `rightClick(By)` | Right-click (context menu) on an element |
| `doubleClick(By / WebElement)` | Double-click an element |
| `hoverOverElement(By / WebElement)` | Hover mouse over an element |
| `dragAndDrop(By source, By target)` | Drag source and drop onto target |
| `scrollToElement(By)` | Scroll element into viewport |

### Checkbox & Radio

| Method | Description |
|--------|-------------|
| `checkCheckbox(By)` | Check a checkbox if not already checked |
| `uncheckCheckbox(By)` | Uncheck a checkbox if currently checked |
| `selectRadioButton(By)` | Select a radio button if not already selected |

### Dropdown Handling

All dropdown methods accept both `By` and `WebElement`.

| Method | Description |
|--------|-------------|
| `selectFromDropDownMenu(By / WebElement, by, value)` | Select a dropdown option |
| `deselectFromDropDownMenu(By / WebElement, by, value)` | Deselect a dropdown option |

**Selection strategies:** `"index"`, `"value"`, `"visible text"`, `"contains text"`

### Element State Validation

| Method | Returns | Description |
|--------|---------|-------------|
| `validateElementIsDisplayed(element)` | `boolean` | Is the element visible? |
| `validateElementIsEnabled(element)` | `boolean` | Is the element interactable? |
| `validateElementIsSelected(element)` | `boolean` | Is the element selected? |

### Wait Management

| Method | Description |
|--------|-------------|
| `setImplicitWait(Duration)` | Set implicit wait using `Duration` object |
| `setImplicitWait("seconds", 5)` | Set implicit wait using readable string |
| `explicitWait(By, timeoutSeconds)` | Wait for element presence with custom timeout |
| `fluentWait(By, timeout, pollingMs, message)` | Fluent wait with polling interval and custom message |

**Supported time units for implicit wait:** `"seconds"`, `"minutes"`, `"hours"`, `"days"`, `"ms"`, `"ns"`

---

## Usage Example

```java
// Initialize with Chrome
ASM_Framework driver = new ASM_Framework("chrome");
driver.manageScreenSize("maximize");
driver.goToURL("https://the-internet.herokuapp.com/login");

// Login using By locator directly
driver.writeInElement(By.id("username"), "tomsmith");
driver.writeInElement(By.id("password"), "SuperSecretPassword!");
driver.clickElement(By.xpath("//button[@type='submit']"));

// Validate result
System.out.println(driver.getElementText(By.cssSelector(".flash.success")));

// Dropdown
driver.selectFromDropDownMenu(By.id("dropdown"), "index", "1");

// Checkbox
driver.checkCheckbox(By.id("agreeTerms"));

// Hover then click
driver.hoverOverElement(By.id("menu"));
driver.clickElement(By.id("menuItem"));

driver.closeAllTabs();
```

---

## Author

**ASMahrous** — Built as part of a Selenium automation learning journey.

Feel free to fork, use, or contribute!

**ASM** — Built as part of a Selenium automation learning journey.

Feel free to fork, use, or contribute!
