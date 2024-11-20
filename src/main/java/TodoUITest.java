import io.qameta.allure.Allure;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TodoUITest {
    private WebDriver driver;

    @BeforeEach
    public void setup() {
        //ChromeOptions options = new ChromeOptions();
        //options.addArguments("--start-maximized");
        driver = new ChromeDriver();
    }

    @AfterEach
    /*public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }*/

    @Test
    public void testTodoMVC() throws Exception {
        String baseUrl = "https://todomvc.com/examples/react/dist/";
        driver.get(baseUrl);

        // Validate the URL
        Assertions.assertEquals(baseUrl, driver.getCurrentUrl(), "Failed to navigate to TodoMVC");
        takeScreenshot("Initial Load");

        // Add TODO item for today
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String todo1 = "TODO 1 - " + today;
        WebElement newTodoInput = driver.findElement(By.className("new-todo"));
        newTodoInput.sendKeys(todo1);
        newTodoInput.sendKeys(Keys.ENTER);
        takeScreenshot("Added TODO 1");

        // Verify TODO 1 appears in the list
        Assertions.assertTrue(driver.findElement(By.xpath("//li[.='" + todo1 + "']")).isDisplayed(), "TODO 1 not found in the list");

        // Add TODO item for tomorrow
        String tomorrow = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);
        String todo2 = "TODO 2 - " + tomorrow;
        newTodoInput.sendKeys(todo2);
        newTodoInput.sendKeys(Keys.ENTER);
        takeScreenshot("Added TODO 2");

        // Verify TODO 2 appears in the list
        Assertions.assertTrue(driver.findElement(By.xpath("//li[.='" + todo2 + "']")).isDisplayed(), "TODO 2 not found in the list");

        // Mark TODO 1 as completed
        WebElement todo1Checkbox = driver.findElement(By.xpath("//li[.='" + todo1 + "']//input[@class='toggle']"));
        todo1Checkbox.click();
        takeScreenshot("Marked TODO 1 as Completed");

        // Verify TODO 1 is displayed as completed
        WebElement todo1Item = driver.findElement(By.xpath("//li[.='" + todo1 + "']"));
        Assertions.assertTrue(todo1Item.getAttribute("class").contains("completed"), "TODO 1 is not marked as completed");

        // Delete TODO 2
        WebElement todo2Item = driver.findElement(By.xpath("//li[.='" + todo2 + "']"));
        Actions actions = new Actions(driver);
        actions.moveToElement(todo2Item).perform();
        WebElement deleteButton = driver.findElement(By.xpath("//li[.='" + todo2 + "']//button[@class='destroy']"));
        deleteButton.click();
        //takeScreenshot("Deleted TODO 2");

        // Verify TODO 2 is removed
        Assertions.assertTrue(driver.findElements(By.xpath("//li[.='" + todo2 + "']")).isEmpty(), "TODO 2 was not removed");
        //Close browser as finished test
       /* if (driver != null) {
            driver.quit();
        }*/
    }

    private void takeScreenshot(String stepName) throws Exception {
        Path screenshotPath = Files.createTempFile("screenshot-", ".png");
        Files.write(screenshotPath, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        Allure.addAttachment(stepName, new ByteArrayInputStream(Files.readAllBytes(screenshotPath)));
    }
}