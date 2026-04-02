# ASM Selenium Framework

A lightweight Java wrapper around Selenium WebDriver that provides a clean, readable API for browser automation — built to reduce boilerplate and make test scripts easier to write and maintain.

---

## Features

- **Multi-browser support** — Chrome, Firefox, Edge, Safari
- **Smart element finding** — supports ID, Name, Class, XPath, CSS Selector
- **Built-in Explicit Waits** — clickable and visibility checks before interactions
- **Flexible Implicit Wait** — set wait duration using human-readable strings like `"seconds"` or `"ms"`
- **Dropdown handling** — select/deselect by index, value, visible text, or partial text
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
git clone https://github.com/YOUR_USERNAME/selenium-java-automation-framework.git
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

### Element Interaction

| Method | Description |
|--------|-------------|
| `findElement(by, locator)` | Find an element by strategy and value |
| `clickElement(element)` | Wait until clickable, then click |
| `writeInElement(element, text)` | Clear field and type text |
| `clearElementText(element)` | Clear an input field |
| `getElementText(element)` | Get visible text of an element |

### Dropdown Handling

| Method | Description |
|--------|-------------|
| `selectFromDropDownMenu(element, by, value)` | Select a dropdown option |
| `deselectFromDropDownMenu(element, by, value)` | Deselect a dropdown option |

**Selection strategies:** `"index"`, `"value"`, `"visible text"`, `"contains text"`

### Element State

| Method | Returns | Description |
|--------|---------|-------------|
| `validateElementIsDisplayed(element)` | `boolean` | Is the element visible? |
| `validateElementIsEnabled(element)` | `boolean` | Is the element interactable? |
| `validateElementIsSelected(element)` | `boolean` | Is the element selected? |

### Wait Management

| Method | Description |
|--------|-------------|
| `setImplicitWait(Duration)` | Set implicit wait using Duration object |
| `setImplicitWait("seconds", 5)` | Set implicit wait using readable string |

**Supported time units:** `"seconds"`, `"minutes"`, `"hours"`, `"days"`, `"ms"`, `"ns"`

---

## Usage Example

```java
// Initialize with Chrome
ASM_Framework driver = new ASM_Framework("chrome");
driver.manageScreenSize("maximize");
driver.goToURL("https://the-internet.herokuapp.com/login");

// Login
WebElement username = driver.findElement("id", "username");
WebElement password = driver.findElement("id", "password");
WebElement loginBtn = driver.findElement("xpath", "//button[@type='submit']");

driver.writeInElement(username, "tomsmith");
driver.writeInElement(password, "SuperSecretPassword!");
driver.clickElement(loginBtn);

// Validate result
WebElement message = driver.findElement("css", ".flash.success");
System.out.println(driver.getElementText(message));

driver.closeAllTabs();
```

---

## Author

**ASM** — Built as part of a Selenium automation learning journey.

Feel free to fork, use, or contribute!
