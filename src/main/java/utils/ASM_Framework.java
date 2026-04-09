package utils;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * ASM_Framework — A Selenium WebDriver wrapper that simplifies browser automation.
 *
 * <p>Provides a clean, readable API for common browser interactions including:
 * element finding, clicking, typing, dropdown handling, and smart waiting.</p>
 *
 * <p><b>Basic usage:</b></p>
 * <pre>{@code
 * ASM_Framework driver = new ASM_Framework("chrome");
 * driver.goToURL("https://example.com");
 * driver.manageScreenSize("maximize");
 *
 * WebElement usernameField = driver.findElement("id", "username");
 * driver.writeInElement(usernameField, "myUser");
 *
 * driver.closeAllTabs();
 * }</pre>
 *
 * <p><b>Usage with browser options:</b></p>
 * <pre>{@code
 * ASM_Framework driver = new ASM_Framework("chrome",
 *     new ASM_Framework.BrowserOptions()
 *         .headless()
 *         .maximized()
 *         .withUserDataDir("/path/to/profile")
 *         .withArgument("--disable-notifications")
 * );
 * }</pre>
 *
 * @author ASMahrous
 * @version 2.0
 */
public class ASM_Framework
{
    private final WebDriver browser;

    /**
     * Default timeout used by all internal Explicit Wait calls (10 seconds).
     * Adjust this value if your application consistently loads slower.
     */
    private final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    // ========================
    // Browser Options Builder
    // ========================

    /**
     * Fluent builder for configuring browser launch options.
     *
     * <p>Pass an instance to the {@link ASM_Framework#ASM_Framework(String, BrowserOptions)}
     * constructor. Only Chrome, Firefox, and Edge are supported for options;
     * Safari uses its default configuration.</p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * BrowserOptions opts = new ASM_Framework.BrowserOptions()
     *     .headless()
     *     .maximized()
     *     .withUserDataDir("C:/Users/me/chrome-profile")
     *     .withArgument("--disable-infobars");
     *
     * ASM_Framework driver = new ASM_Framework("chrome", opts);
     * }</pre>
     */
    public static class BrowserOptions
    {

        boolean headless    = false;
        boolean kiosk       = false;
        boolean maximized   = false;
        String  userDataDir = null;
        final List<String> extraArguments = new ArrayList<>();

        /**
         * Runs the browser in headless mode (no visible UI window).
         * Ideal for CI pipelines and server environments.
         *
         * @return this {@code BrowserOptions} instance (fluent)
         */
        public BrowserOptions headless()
        {
            this.headless = true;
            return this;
        }

        /**
         * Launches the browser in kiosk (full-screen, borderless) mode.
         * Note: kiosk and headless are mutually exclusive — kiosk takes precedence
         * when both are set.
         *
         * @return this {@code BrowserOptions} instance (fluent)
         */
        public BrowserOptions kiosk()
        {
            this.kiosk = true;
            return this;
        }

        /**
         * Launches the browser in a maximized window.
         * Has no effect when combined with headless mode.
         *
         * @return this {@code BrowserOptions} instance (fluent)
         */
        public BrowserOptions maximized()
        {
            this.maximized = true;
            return this;
        }

        /**
         * Sets a custom user-data directory so the browser loads a specific profile.
         *
         * <p>Useful for preserving cookies, extensions, and session state between runs.</p>
         *
         * @param path absolute path to the Chrome/Firefox profile directory
         * @return this {@code BrowserOptions} instance (fluent)
         */
        public BrowserOptions withUserDataDir(String path)
        {
            this.userDataDir = path;
            return this;
        }

        /**
         * Appends an arbitrary browser argument (e.g., {@code "--disable-notifications"}).
         * Can be called multiple times to add several arguments.
         *
         * @param argument the full argument string including leading dashes
         * @return this {@code BrowserOptions} instance (fluent)
         */
        public BrowserOptions withArgument(String argument)
        {
            this.extraArguments.add(argument);
            return this;
        }
    }

    // ========================
    // Constructors
    // ========================

    /**
     * Initializes the framework with the specified browser using default settings.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognized.</p>
     *
     * @param browserName the browser to launch (e.g., "chrome", "firefox")
     */
    public ASM_Framework(String browserName)
    {
        this(browserName, new BrowserOptions());
    }

    /**
     * Initializes the framework with the specified browser and custom launch options.
     *
     * <p>Supported values (case-insensitive): {@code "chrome"}, {@code "firefox"},
     * {@code "edge"}, {@code "safari"}. Defaults to Chrome if unrecognized.
     * Safari ignores the {@code options} parameter.</p>
     *
     * @param browserName the browser to launch (e.g., "chrome", "firefox")
     * @param options     a {@link BrowserOptions} instance configuring headless, kiosk, etc.
     */
    public ASM_Framework(String browserName, BrowserOptions options)
    {
        switch (browserName.toLowerCase())
        {
            case "edge":
                browser = new EdgeDriver(buildEdgeOptions(options));
                break;

            case "safari":
                // Safari's driver does not expose an Options API equivalent to Chrome/Firefox
                browser = new SafariDriver();
                break;

            case "firefox":
                browser = new FirefoxDriver(buildFirefoxOptions(options));
                break;

            default:
                browser = new ChromeDriver(buildChromeOptions(options));
        }
    }

    // ==========================
    // Options Builders (private)
    // ==========================

    /**
     * Translates a {@link BrowserOptions} instance into a Selenium {@link ChromeOptions} object.
     */
    private ChromeOptions buildChromeOptions(BrowserOptions opts)
    {
        ChromeOptions chromeOptions = new ChromeOptions();

        if (opts.kiosk)
        {
            chromeOptions.addArguments("--kiosk");
        }
        else if (opts.headless)
        {
            // --headless=new is the modern headless mode available from Chrome 112+
            chromeOptions.addArguments("--headless=new");
        }

        if (opts.maximized)
        {
            chromeOptions.addArguments("--start-maximized");
        }

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
        {
            chromeOptions.addArguments("--user-data-dir=" + opts.userDataDir);
        }

        for (String arg : opts.extraArguments)
        {
            chromeOptions.addArguments(arg);
        }

        return chromeOptions;
    }

    /**
     * Translates a {@link BrowserOptions} instance into a Selenium {@link FirefoxOptions} object.
     */
    private FirefoxOptions buildFirefoxOptions(BrowserOptions opts)
    {
        FirefoxOptions firefoxOptions = new FirefoxOptions();

        if (opts.headless && !opts.kiosk)
        {
            firefoxOptions.addArguments("--headless");
        }

        if (opts.maximized)
        {
            firefoxOptions.addArguments("--start-maximized");
        }

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
        {
            // Firefox uses -profile rather than --user-data-dir
            firefoxOptions.addArguments("-profile", opts.userDataDir);
        }

        for (String arg : opts.extraArguments)
        {
            firefoxOptions.addArguments(arg);
        }

        return firefoxOptions;
    }

    /**
     * Translates a {@link BrowserOptions} instance into a Selenium {@link EdgeOptions} object.
     */
    private EdgeOptions buildEdgeOptions(BrowserOptions opts)
    {
        EdgeOptions edgeOptions = new EdgeOptions();

        if (opts.kiosk)
        {
            edgeOptions.addArguments("--kiosk");
        }
        else if (opts.headless)
        {
            edgeOptions.addArguments("--headless=new");
        }

        if (opts.maximized)
        {
            edgeOptions.addArguments("--start-maximized");
        }

        if (opts.userDataDir != null && !opts.userDataDir.isEmpty())
        {
            edgeOptions.addArguments("--user-data-dir=" + opts.userDataDir);
        }

        for (String arg : opts.extraArguments)
        {
            edgeOptions.addArguments(arg);
        }

        return edgeOptions;
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
     * Converts a locator strategy string and value into a Selenium {@link By} object.
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
     * @param locatorType the locator strategy (e.g., "id", "xpath")
     * @param locator     the locator value (e.g., "loginBtn", "//button[@type='submit']")
     * @return the corresponding {@link By} object
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public By getBy(String locatorType, String locator)
    {
        switch (locatorType.toLowerCase())
        {
            case "id":
                return By.id(locator);

            case "name":
                return By.name(locator);

            case "class name":
            case "class":
                return By.className(locator);

            case "xpath":
                return By.xpath(locator);

            case "css selector":
            case "css":
                return By.cssSelector(locator);

            default:
                throw new IllegalArgumentException("Invalid locator type: " + locatorType);
        }
    }

    /**
     * Waits for the element to be present in the DOM (using {@link #DEFAULT_TIMEOUT}),
     * then returns it.
     *
     * <p>Supported locator types (case-insensitive):
     * {@code "id"}, {@code "name"}, {@code "class"}, {@code "xpath"}, {@code "css"}</p>
     *
     * @param locatorType the locator strategy (e.g., "id", "xpath")
     * @param locator     the locator value (e.g., "loginBtn", "//button[@type='submit']")
     * @return the found WebElement once it is present in the DOM
     * @throws TimeoutException         if the element is not found within {@link #DEFAULT_TIMEOUT}
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public WebElement findElement(String locatorType, String locator)
    {
        By by = getBy(locatorType, locator);
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    /**
     * Waits up to {@code timeoutSeconds} for the element to be present in the DOM,
     * then returns it.
     *
     * @param locatorType    the locator strategy (e.g., "id", "xpath")
     * @param locator        the locator value
     * @param timeoutSeconds how long to wait before throwing {@link TimeoutException}
     * @return the found WebElement once it is present in the DOM
     * @throws TimeoutException         if the element is not found within the given timeout
     * @throws IllegalArgumentException if the locator type is not recognized
     */
    public WebElement findElement(String locatorType, String locator, long timeoutSeconds)
    {
        By by = getBy(locatorType, locator);
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    // ========================
    // Element Interaction
    // ========================

    /**
     * Finds the element by locator, clears it, then types the given text.
     *
     * @param locator the By locator of the target input element
     * @param text    the text to type
     */
    public void writeInElement(By locator, String text)
    {
        WebElement element = waitForElementToBeVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

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
     * Finds the element by locator and clears its text content.
     *
     * @param locator the By locator of the target input element
     */
    public void clearElementText(By locator)
    {
        waitForElementToBeVisible(locator).clear();
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
     * Finds the element by locator and returns its visible text.
     *
     * @param locator the By locator of the target element
     * @return the element's visible text
     */
    public String getElementText(By locator)
    {
        return waitForElementToBeVisible(locator).getText();
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

    /**
     * Waits for the element to be clickable, then clicks it.
     *
     * @param locator the By locator of the element to click
     */
    public void clickElement(By locator)
    {
        waitForElementToBeClickable(locator).click();
    }

    /**
     * Performs a double click on the specified element.
     *
     * @param locator the By locator of the element to double-click
     */
    public void doubleClick(By locator)
    {
        WebElement element = waitForElementToBeClickable(locator);
        new Actions(browser)
                .doubleClick(element)
                .perform();
    }

    /**
     * Performs a double click on the specified element.
     *
     * @param elementLocator the WebElement to double-click
     */
    public void doubleClick(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        new Actions(browser)
                .doubleClick(elementLocator)
                .perform();
    }

    /**
     * Performs a right-click (context click) on the specified element.
     *
     * @param locator the By locator of the element to right-click
     */
    public void rightClick(By locator)
    {
        WebElement element = waitForElementToBeVisible(locator);
        new Actions(browser)
                .contextClick(element)
                .perform();
    }

    /**
     * Drags an element from source and drops it onto the target element.
     *
     * @param sourceLocator the By locator of the element to drag
     * @param targetLocator the By locator of the drop target
     */
    public void dragAndDrop(By sourceLocator, By targetLocator)
    {
        WebElement source = waitForElementToBeVisible(sourceLocator);
        WebElement target = waitForElementToBeVisible(targetLocator);
        new Actions(browser)
                .dragAndDrop(source, target)
                .perform();
    }

    /**
     * Scrolls the viewport to bring the specified element into view.
     *
     * @param locator the By locator of the element to scroll to
     */
    public void scrollToElement(By locator)
    {
        WebElement element = waitForElementToBeVisible(locator);
        new Actions(browser)
                .scrollToElement(element)
                .perform();
    }

    /**
     * Hovers the mouse over the specified element (mouse-over / tooltip trigger).
     *
     * @param locator the By locator of the element to hover over
     */
    public void hoverOverElement(By locator)
    {
        WebElement element = waitForElementToBeVisible(locator);
        new Actions(browser)
                .moveToElement(element)
                .perform();
    }

    /**
     * Hovers the mouse over the specified element (mouse-over / tooltip trigger).
     *
     * @param elementLocator the WebElement to hover over
     */
    public void hoverOverElement(WebElement elementLocator)
    {
        validateElementIsFound(elementLocator);
        new Actions(browser)
                .moveToElement(elementLocator)
                .perform();
    }

    // =========================
    // Checkbox & Radio Buttons
    // =========================

    /**
     * Checks a checkbox if it is not already checked.
     *
     * @param locator the By locator of the checkbox element
     */
    public void checkCheckbox(By locator)
    {
        WebElement checkbox = waitForElementToBeClickable(locator);
        if (!checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Unchecks a checkbox if it is currently checked.
     *
     * @param locator the By locator of the checkbox element
     */
    public void uncheckCheckbox(By locator)
    {
        WebElement checkbox = waitForElementToBeClickable(locator);
        if (checkbox.isSelected())
        {
            checkbox.click();
        }
    }

    /**
     * Selects a radio button if it is not already selected.
     *
     * @param locator the By locator of the radio button element
     */
    public void selectRadioButton(By locator)
    {
        WebElement radioButton = waitForElementToBeClickable(locator);
        if (!radioButton.isSelected())
        {
            radioButton.click();
        }
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
     * Finds the dropdown by locator, then selects an option using the specified strategy.
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
     * @param locator     the By locator of the dropdown element
     * @param selectBy    the selection strategy
     * @param selectInput the value to select by (index, value string, or text)
     */
    public void selectFromDropDownMenu(By locator, String selectBy, String selectInput)
    {
        selectFromDropDownMenu(waitForElementToBeVisible(locator), selectBy, selectInput);
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

    /**
     * Finds the dropdown by locator, then deselects an option using the specified strategy.
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
     * @param locator       the By locator of the dropdown element
     * @param deselectBy    the deselection strategy
     * @param deselectInput the value to deselect by (ignored when strategy is "all")
     */
    public void deselectFromDropDownMenu(By locator, String deselectBy, String deselectInput)
    {
        deselectFromDropDownMenu(waitForElementToBeVisible(locator), deselectBy, deselectInput);
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
     * Sets a global Implicit Wait using a plain seconds value.
     *
     * <p>Convenience overload — equivalent to
     * {@code setImplicitWait(Duration.ofSeconds(seconds))}.</p>
     *
     * <p><b>Warning:</b> Avoid mixing Implicit and Explicit Waits.</p>
     *
     * @param seconds number of seconds to wait
     */
    public void setImplicitWait(long seconds)
    {
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
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

    /**
     * Explicitly waits for the presence of an element in the DOM (seconds overload).
     *
     * @param locator        the By locator of the element
     * @param timeoutSeconds how long to wait before throwing TimeoutException
     */
    public void setExplicitWait(By locator, long timeoutSeconds)
    {
        WebDriverWait wait = new WebDriverWait(browser, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Explicitly waits for the presence of an element in the DOM ({@link Duration} overload).
     *
     * @param locator  the By locator of the element
     * @param timeout  how long to wait before throwing TimeoutException
     */
    public void setExplicitWait(By locator, Duration timeout)
    {
        WebDriverWait wait = new WebDriverWait(browser, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element to be present using a Fluent Wait strategy (seconds overload).
     * Polls repeatedly until the element appears or the timeout is reached.
     *
     * @param locator        the By locator of the element
     * @param timeoutSeconds maximum time to wait
     * @param pollingMillis  how often to check (in milliseconds)
     * @param timeoutMessage custom message if timeout is reached
     */
    public void setFluentWait(By locator, long timeoutSeconds, long pollingMillis, String timeoutMessage)
    {
        new FluentWait<>(browser)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .withMessage(timeoutMessage)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits for an element to be present using a Fluent Wait strategy ({@link Duration} overload).
     * Polls repeatedly until the element appears or the timeout is reached.
     *
     * @param locator         the By locator of the element
     * @param timeout         maximum time to wait
     * @param pollingInterval how often to check
     * @param timeoutMessage  custom message if timeout is reached
     */
    public void setFluentWait(By locator, Duration timeout, Duration pollingInterval, String timeoutMessage)
    {
        new FluentWait<>(browser)
                .withTimeout(timeout)
                .pollingEvery(pollingInterval)
                .ignoring(NoSuchElementException.class)
                .withMessage(timeoutMessage)
                .until(ExpectedConditions.presenceOfElementLocated(locator));
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

    // ========================
    // Window Handling
    // ========================

    /**
     * Returns the handle (unique ID) of the currently focused browser window or tab.
     *
     * <p>Store this handle before opening new windows so you can switch back later:</p>
     * <pre>{@code
     * String mainWindow = driver.getCurrentWindowHandle();
     * driver.clickElement(By.id("openPopup"));
     * driver.switchToNewWindow(mainWindow);
     * // ... interact with popup ...
     * driver.switchToWindowByHandle(mainWindow);
     * }</pre>
     *
     * @return the current window handle string
     */
    public String getCurrentWindowHandle()
    {
        return browser.getWindowHandle();
    }

    /**
     * Returns all open window/tab handles as an ordered list.
     *
     * <p>Windows are returned in the order they were opened, making
     * index-based switching predictable:</p>
     * <pre>{@code
     * driver.switchToWindowByIndex(1); // second tab
     * }</pre>
     *
     * @return a {@link List} of all open window handle strings
     */
    public List<String> getAllWindowHandles()
    {
        return new ArrayList<>(browser.getWindowHandles());
    }

    /**
     * Switches focus to a window or tab by its handle string.
     *
     * @param windowHandle the handle of the target window (from {@link #getCurrentWindowHandle()}
     *                     or {@link #getAllWindowHandles()})
     */
    public void switchToWindowByHandle(String windowHandle)
    {
        browser.switchTo().window(windowHandle);
    }

    /**
     * Switches focus to a window or tab by its position index (0-based).
     *
     * <p>Index {@code 0} is always the first opened window/tab.</p>
     *
     * @param index the 0-based index of the target window
     * @throws IndexOutOfBoundsException if the index exceeds the number of open windows
     */
    public void switchToWindowByIndex(int index)
    {
        List<String> handles = getAllWindowHandles();
        browser.switchTo().window(handles.get(index));
    }

    /**
     * Switches focus to the newest window or tab that was opened after the given parent handle.
     *
     * <p>Typical pattern — click a link that opens a popup, then call this method:</p>
     * <pre>{@code
     * String parent = driver.getCurrentWindowHandle();
     * driver.clickElement(By.linkText("Open in new tab"));
     * driver.switchToNewWindow(parent);
     * }</pre>
     *
     * @param parentHandle the handle of the originating window to exclude from the search
     * @throws RuntimeException if no new window is found
     */
    public void switchToNewWindow(String parentHandle)
    {
        Set<String> allHandles = browser.getWindowHandles();
        for (String handle : allHandles)
        {
            if (!handle.equals(parentHandle))
            {
                browser.switchTo().window(handle);
                return;
            }
        }
        throw new RuntimeException("No new window found after switching from handle: " + parentHandle);
    }

    /**
     * Closes the currently focused window or tab, then switches focus back to the given handle.
     *
     * <p>Useful for closing a popup and returning to the main window:</p>
     * <pre>{@code
     * String mainWindow = driver.getCurrentWindowHandle();
     * driver.switchToNewWindow(mainWindow);
     * // ... interact with popup ...
     * driver.closeCurrentWindowAndSwitchTo(mainWindow);
     * }</pre>
     *
     * @param windowHandleToSwitchTo the handle to focus after closing the current window
     */
    public void closeCurrentWindowAndSwitchTo(String windowHandleToSwitchTo)
    {
        browser.close();
        browser.switchTo().window(windowHandleToSwitchTo);
    }

    /**
     * Returns the total number of currently open windows or tabs.
     *
     * @return the count of open window handles
     */
    public int getWindowCount()
    {
        return browser.getWindowHandles().size();
    }

    // ========================
    // Alert Handling
    // ========================

    /**
     * Waits for a JavaScript alert/confirm/prompt to appear, then returns it.
     *
     * <p>Internal helper used by all public alert methods.</p>
     *
     * @return the {@link Alert} object once it is present
     */
    private Alert waitForAlert()
    {
        WebDriverWait wait = new WebDriverWait(browser, DEFAULT_TIMEOUT);
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Accepts (clicks OK on) the currently open JavaScript alert, confirm, or prompt dialog.
     *
     * <p>Use this for:
     * <ul>
     *   <li>Alert — dismisses the dialog</li>
     *   <li>Confirm — clicks OK (returns {@code true} to the page)</li>
     *   <li>Prompt — submits whatever text was typed (or empty string)</li>
     * </ul>
     * </p>
     */
    public void acceptAlert()
    {
        waitForAlert().accept();
    }

    /**
     * Dismisses (clicks Cancel on) the currently open JavaScript confirm or prompt dialog.
     *
     * <p>Note: calling dismiss on a plain alert has the same effect as accepting it.</p>
     */
    public void dismissAlert()
    {
        waitForAlert().dismiss();
    }

    /**
     * Returns the message text displayed inside the current JavaScript alert dialog.
     *
     * @return the alert message string
     */
    public String getAlertText()
    {
        return waitForAlert().getText();
    }

    /**
     * Types text into a JavaScript prompt dialog, then accepts it.
     *
     * <p>Example:</p>
     * <pre>{@code
     * driver.typeInAlert("John Doe"); // enters name and clicks OK
     * }</pre>
     *
     * @param text the text to enter into the prompt input field
     */
    public void typeInAlert(String text)
    {
        Alert alert = waitForAlert();
        alert.sendKeys(text);
        alert.accept();
    }

    // ========================
// IFrame Handling
// ========================

    /**
     * Switches the WebDriver context into an iframe located by a {@link By} locator.
     *
     * <p>After calling this, all subsequent {@code findElement} calls will search
     * inside the iframe. Remember to call {@link #switchToDefaultContent()} when done.</p>
     *
     * @param locator the By locator of the {@code <iframe>} element
     */
    public void switchToIFrame(By locator)
    {
        WebElement iFrame = waitForElementToBeVisible(locator);
        browser.switchTo().frame(iFrame);
    }

    /**
     * Switches the WebDriver context into an iframe using a {@link WebElement} reference.
     *
     * @param iFrameElement the WebElement representing the {@code <iframe>}
     */
    public void switchToIFrame(WebElement iFrameElement)
    {
        validateElementIsFound(iFrameElement);
        browser.switchTo().frame(iFrameElement);
    }

    /**
     * Switches the WebDriver context into an iframe by its 0-based index on the page.
     *
     * <p>Use when multiple iframes exist and you want to target one by position.</p>
     *
     * @param index the 0-based index of the iframe on the page
     */
    public void switchToIFrameByIndex(int index)
    {
        browser.switchTo().frame(index);
    }

    /**
     * Switches the WebDriver context into an iframe by its {@code name} or {@code id} attribute.
     *
     * @param nameOrId the value of the iframe's {@code name} or {@code id} attribute
     */
    public void switchToIFrameByNameOrId(String nameOrId)
    {
        browser.switchTo().frame(nameOrId);
    }

    /**
     * Switches the WebDriver context out of the current iframe and back to the
     * top-level page (default content).
     *
     * <p>Always call this after finishing interactions inside an iframe.</p>
     */
    public void switchToDefaultContent()
    {
        browser.switchTo().defaultContent();
    }

    /**
     * Switches the WebDriver context one level up — from a nested iframe to
     * its immediate parent frame (or the main page if not nested).
     */
    public void switchToParentFrame()
    {
        browser.switchTo().parentFrame();
    }


    // ========================
    // Screenshots
    // ========================

    /**
     * Captures a screenshot of the current browser state and saves it to the
     * {@code Screenshots/} folder at the project root.
     *
     * <p>The file is named using the provided label and a timestamp so each
     * screenshot is uniquely identifiable and never overwrites another:</p>
     * <pre>
     * Screenshots/
     * └── LoginPage_2025-07-21_14-35-22-123.png
     * </pre>
     *
     * <p>The {@code Screenshots/} directory is created automatically if it does
     * not already exist.</p>
     *
     * <p>Example:</p>
     * <pre>{@code
     * driver.takeScreenshot("LoginPage");
     * driver.takeScreenshot("AfterSubmit");
     * }</pre>
     *
     * @param screenshotLabel a short descriptive label used as the filename prefix
     *                        (e.g., "LoginPage", "ErrorState"). Spaces are replaced
     *                        with underscores automatically.
     * @throws RuntimeException if the screenshot cannot be saved due to an I/O error
     */
    public void takeScreenshot(String screenshotLabel)
    {
        // Build the Screenshots folder path relative to the project root
        Path screenshotsDir = Paths.get(System.getProperty("user.dir"), "Screenshots");

        // Create the directory if it does not exist yet
        try
        {
            Files.createDirectories(screenshotsDir);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to create Screenshots directory: " + screenshotsDir, e);
        }

        // Build a timestamp string: yyyy-MM-dd_HH-mm-ss-SSS
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS"));

        // Sanitize the label — replace spaces with underscores
        String sanitizedLabel = screenshotLabel.trim().replace(" ", "_");

        // Assemble the full file path
        String fileName = sanitizedLabel + "_" + timestamp + ".png";
        Path destination = screenshotsDir.resolve(fileName);

        // Capture and save the screenshot
        File sourceFile = ((TakesScreenshot) browser).getScreenshotAs(OutputType.FILE);
        try
        {
            Files.copy(sourceFile.toPath(), destination);
            System.out.println("Screenshot saved: " + destination);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to save screenshot to: " + destination, e);
        }
    }
}