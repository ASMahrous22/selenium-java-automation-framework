package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * ASM_Framework — A Selenium WebDriver wrapper that simplifies browser automation.
 *
 * <p>Provides a clean, readable API for common browser interactions including:
 * element finding, clicking, typing, dropdown handling, and smart waiting.</p>
 *
 * <p><b>Usage Example:</b></p>
 * <pre>{@code
 * ASM_Framework driver = new ASM_Framework("chrome");
 * driver.goToURL("https://example.com");
 * driver.manageScreenSize("maximize");
 *
 * WebElement usernameField = driver.findElement("id", "username");
 * driver.writeInElement(usernameField, "myUser");
 *
 * WebElement loginBtn = driver.findElement("xpath", "//button[@type='submit']");
 * driver.clickElement(loginBtn);
 *
 * driver.closeAllTabs();
 * }</pre>
 *
 * @author ASM
 * @version 1.0
 */
public class ASM_Framework {

    private final WebDriver browser;

    /**
     * Default timeout used by all Explicit Wait methods (10 seconds).
     * Adjust this value if your application consistently loads slower.
     */
    private final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // ========================
    // Constructor
    // ========================

    /**
     * Initializes the framework with the specified browser.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognized.</p>
     *
     * @param browserName the browser to launch (e.g., "chrome", "firefox")
     */
    public ASM_Framework(String browserName)
    {
        switch (browserName.toLowerCase())
        {
            case "edge":
                browser = new EdgeDriver();
                break;

            case "safari":
                browser = new SafariDriver();
                break;

            case "firefox":
                browser = new FirefoxDriver();
                break;

            default:
                browser = new ChromeDriver();
        }
    }

    // ========================
    // Browser Management
    // ========================

    /**
     * Closes the currently focused browser tab.
     * If only one tab is open, this effectively closes the browser.
     */
    public void closeCurrentTab()
    {
        browser.close();
    }

    /**
     * Closes all open browser tabs and ends the WebDriver session.
     * Always call this at the end of your test to release resources.
     */
    public void closeAllTabs()
    {
        browser.quit();
    }

    /**
     * Navigates the browser to the given URL.
     *
     * @param url the full URL to open (e.g., "https://example.com")
     */
    public void goToURL(String url)
    {
        browser.get(url);
    }

    /**
     * Returns the title of the current page (the text shown in the browser tab).
     *
     * @return the current page title
     */
    public String getCurrentPageTitle()
    {
        return browser.getTitle();
    }

    /**
     * Returns the full URL of the current page.
     *
     * @return the current page URL
     */
    public String getCurrentPageURL()
    {
        return browser.getCurrentUrl();
    }

    /**
     * Simulates clicking browser navigation buttons.
     *
     * <p>Supported values (case-insensitive):
     * <ul>
     *   <li>{@code "back"} — go to the previous page</li>
     *   <li>{@code "forward"} — go to the next page</li>
     *   <li>{@code "refresh"} — reload the current page</li>
     * </ul>
     * </p>
     *
     * @param button the navigation action to perform
     */
    public void manageNavigationButtons(String button)
    {
        switch (button.toLowerCase())
        {
            case "back":
                browser.navigate().back();
                break;

            case "refresh":
                browser.navigate().refresh();
                break;

            case "forward":
                browser.navigate().forward();
                break;

            default:
        }
    }

    /**
     * Controls the browser window size.
     *
     * <p>Supported values (case-insensitive):
     * <ul>
     *   <li>{@code "minimize"} or {@code "min"} — minimize the window</li>
     *   <li>{@code "maximize"} or {@code "max"} — maximize the window</li>
     *   <li>anything else — enter fullscreen mode</li>
     * </ul>
     * </p>
     *
     * @param action the window size action to perform
     */
    public void manageScreenSize(String action)
    {
        switch (action.toLowerCase())
        {
            case "min":
            case "minimize":
                browser.manage().window().minimize();
                break;

            case "max":
            case "maximize":
                browser.manage().window().maximize();
                break;

            default:
                browser.manage().window().fullscreen();
        }
    }

    // ========================
    // Element Finding
    // ========================

    /**
     * Finds and returns a WebElement using the specified locator strategy.
     *
     * <p>Supported locator types (case-insensitive):
     * <ul>
     *   <li>{@code "id"}</li>
     *   <li>{@code "name"}</li>
     *   <li>{@code "class"} or {@code "class name"}</li>
     *   <li>{@code "xpath"}</li>
     *   <li>{@code "css"} or {@code "css selector"}</li>
     * </ul>
     * </p>
     *
     * @param by      the locator strategy (e.g., "id", "xpath")
     * @param locator the locator value (e.g., "loginBtn", "//button[@type='submit']")
     * @return the found WebElement
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public WebElement findElement(String by, String locator)
    {
        switch (by.toLowerCase())
        {
            case "id":
                return browser.findElement(By.id(locator));

            case "name":
                return browser.findElement(By.name(locator));

            case "class name":
            case "class":
                return browser.findElement(By.className(locator));

            case "xpath":
                return browser.findElement(By.xpath(locator));

            case "css selector":
            case "css":
                return browser.findElement(By.cssSelector(locator));

            default:
                throw new IllegalArgumentException("Invalid locator type: " + by);
        }
    }

    // ========================
    // Element Interaction
    // ========================

    /**
     * Clears any existing text in the element, then types the given text.
     *
     * @param elementLocator the target input WebElement
     * @param text           the text to type
     */
    public void writeInElement(WebElement elementLocator, String text)
    {
        // clearing first to make sure the text field is empty before typing
        clearElementText(elementLocator);
        elementLocator.sendKeys(text);
    }

    /**
     * Clears all text content from the given input element.
     *
     * @param elementLocator the target input WebElement
     */
    public void clearElementText(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        elementLocator.clear();
    }

    /**
     * Returns the visible text content of the given element.
     *
     * @param elementLocator the target WebElement
     * @return the element's visible text
     */
    public String getElementText(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        return elementLocator.getText();
    }

    /**
     * Waits for the element to be clickable, then clicks it.
     *
     * <p>Uses an Explicit Wait with {@link #DEFAULT_TIMEOUT} before clicking.</p>
     *
     * @param elementLocator the target WebElement to click
     */
    public void clickElement(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        waitForElementToBeClickable(elementLocator).click();
    }

    // ========================
    // Dropdown Handling
    // ========================

    /**
     * Selects an option from an HTML {@code <select>} dropdown element.
     *
     * <p>Supported selection strategies (case-insensitive):
     * <ul>
     *   <li>{@code "index"} — select by option index (0-based)</li>
     *   <li>{@code "value"} — select by option {@code value} attribute</li>
     *   <li>{@code "visible"} / {@code "visible text"} — select by exact visible text</li>
     *   <li>{@code "contains"} / {@code "contains text"} — select by partial visible text</li>
     * </ul>
     * </p>
     *
     * @param elementLocator the dropdown WebElement
     * @param selectBy       the selection strategy
     * @param selectInput    the value to select by (index, value string, or text)
     */
    public void selectFromDropDownMenu(WebElement elementLocator, String selectBy, String selectInput)
    {
        validateElementIsFound(elementLocator);
        Select dropDownMenu = new Select(elementLocator);
        switch (selectBy.toLowerCase())
        {
            case "index":
                dropDownMenu.selectByIndex(Integer.parseInt(selectInput));
                break;

            case "value":
                dropDownMenu.selectByValue(selectInput);
                break;

            case "visible text":
            case "visibletext":
            case "visible":
                dropDownMenu.selectByVisibleText(selectInput);
                break;

            case "contains visible text":
            case "containsvisibletext":
            case "contains text":
            case "containstext":
            case "contains":
                dropDownMenu.selectByContainsVisibleText(selectInput);
                break;

            default:
                System.out.println("Invalid selection type: " + selectBy);
        }
    }

    /**
     * Deselects an option from a multi-select HTML {@code <select>} dropdown.
     *
     * <p>Supported deselection strategies (case-insensitive):
     * <ul>
     *   <li>{@code "index"} — deselect by option index</li>
     *   <li>{@code "value"} — deselect by option {@code value} attribute</li>
     *   <li>{@code "visible"} / {@code "visible text"} — deselect by exact visible text</li>
     *   <li>{@code "contains"} / {@code "contains text"} — deselect by partial visible text</li>
     *   <li>{@code "all"} — deselect all selected options</li>
     * </ul>
     * </p>
     *
     * @param elementLocator  the dropdown WebElement
     * @param deselectBy      the deselection strategy
     * @param deselectInput   the value to deselect by (ignored when strategy is "all")
     */
    public void deselectFromDropDownMenu(WebElement elementLocator, String deselectBy, String deselectInput)
    {
        validateElementIsFound(elementLocator);
        Select dropDownMenu = new Select(elementLocator);
        switch (deselectBy.toLowerCase())
        {
            case "index":
                dropDownMenu.deselectByIndex(Integer.parseInt(deselectInput));
                break;

            case "value":
                dropDownMenu.deselectByValue(deselectInput);
                break;

            case "visible text":
            case "visibletext":
            case "visible":
                dropDownMenu.deselectByVisibleText(deselectInput);
                break;

            case "contains visible text":
            case "containsvisibletext":
            case "contains text":
            case "containstext":
            case "contains":
                dropDownMenu.deSelectByContainsVisibleText(deselectInput);
                break;

            case "all":
                dropDownMenu.deselectAll();
                break;

            default:
                System.out.println("Invalid selection type: " + deselectBy);
        }
    }

    // ========================
    // Element State Validation
    // ========================

    /**
     * Checks whether the given element is currently selected (e.g., a checkbox or radio button).
     *
     * @param elementLocator the target WebElement
     * @return {@code true} if selected, {@code false} otherwise
     */
    public boolean validateElementIsSelected(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        return elementLocator.isSelected();
    }

    /**
     * Checks whether the given element is currently visible on the page.
     *
     * @param elementLocator the target WebElement
     * @return {@code true} if displayed, {@code false} otherwise
     */
    public boolean validateElementIsDisplayed(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        return elementLocator.isDisplayed();
    }

    /**
     * Checks whether the given element is currently enabled and interactable.
     *
     * @param elementLocator the target WebElement
     * @return {@code true} if enabled, {@code false} otherwise
     */
    public boolean validateElementIsEnabled(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        return elementLocator.isEnabled();
    }

    // ========================
    // Wait Management
    // ========================

    /**
     * Sets a global Implicit Wait using a {@link Duration} object.
     *
     * <p><b>Warning:</b> Avoid mixing Implicit and Explicit Waits —
     * this can cause unpredictable timeout behavior in Selenium.</p>
     *
     * @param duration the wait duration (e.g., {@code Duration.ofSeconds(5)})
     */
    public void setImplicitWait(Duration duration)
    {
        browser.manage().timeouts().implicitlyWait(duration);
    }

    /**
     * Sets a global Implicit Wait using a human-readable time unit string.
     *
     * <p>Supported time units (case-insensitive):
     * {@code "seconds"/"sec"}, {@code "minutes"/"min"}, {@code "hours"/"hour"},
     * {@code "days"/"day"}, {@code "ms"/"mili"}, {@code "ns"/"nano"}</p>
     *
     * <p><b>Warning:</b> Avoid mixing Implicit and Explicit Waits.</p>
     *
     * @param durationIn  the time unit as a string (e.g., "seconds", "ms")
     * @param waitingTime the amount of time to wait
     * @throws IllegalArgumentException if the time unit is not recognized
     */
    public void setImplicitWait(String durationIn, long waitingTime)
    {
        switch (durationIn.toLowerCase())
        {
            case "day":
            case "days":
                browser.manage().timeouts().implicitlyWait(Duration.ofDays(waitingTime));
                break;

            case "hour":
            case "hours":
                browser.manage().timeouts().implicitlyWait(Duration.ofHours(waitingTime));
                break;

            case "min":
            case "mins":
            case "minute":
            case "minutes":
                browser.manage().timeouts().implicitlyWait(Duration.ofMinutes(waitingTime));
                break;

            case "sec":
            case "secs":
            case "second":
            case "seconds":
                browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(waitingTime));
                break;

            case "ms":
            case "msec":
            case "mili":
            case "milis":
                browser.manage().timeouts().implicitlyWait(Duration.ofMillis(waitingTime));
                break;

            case "ns":
            case "nsec":
            case "nano":
            case "nanos":
                browser.manage().timeouts().implicitlyWait(Duration.ofNanos(waitingTime));
                break;

            default:
                throw new IllegalArgumentException("Invalid time unit: " + durationIn);
        }
    }

    // ========================
    // Private Helpers
    // ========================

    /**
     * Validates that the given WebElement is not null.
     * Throws a descriptive exception to help debug missing {@code findElement()} calls.
     *
     * @param elementLocator the WebElement to validate
     * @throws RuntimeException if the element is null
     */
    private void validateElementIsFound(WebElement elementLocator)
    {
        if (elementLocator == null)
        {
            throw new RuntimeException("Error: element is null, check your locator!");
        }
    }

    /**
     * Waits until the element located by the given {@link By} strategy is clickable.
     *
     * @param locator the By locator strategy
     * @return the clickable WebElement
     */
    private WebElement waitForElementToBeClickable(By locator)
    {
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits until the given WebElement is clickable.
     *
     * @param elementLocator the WebElement to wait for
     * @return the clickable WebElement
     */
    private WebElement waitForElementToBeClickable(WebElement elementLocator)
    {
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.elementToBeClickable(elementLocator));
    }

    /**
     * Waits until the element located by the given {@link By} strategy is visible on the page.
     *
     * @param locator the By locator strategy
     * @return the visible WebElement
     */
    private WebElement waitForElementToBeVisible(By locator)
    {
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the given WebElement is visible on the page.
     *
     * @param elementLocator the WebElement to wait for
     * @return the visible WebElement
     */
    private WebElement waitForElementToBeVisible(WebElement elementLocator)
    {
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.visibilityOf(elementLocator));
    }
}