package pt.teamloan.jobs;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class UpdateZipCodesJob {
    private WebDriver driver;
    private String downloadDirPath;

    @PostConstruct
    void setUp() {
        downloadDirPath = System.getProperty("java.io.tmpdir");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("download.default_directory", downloadDirPath);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        options.setExperimentalOption("prefs", prefs);
        driver = new ChromeDriver(options);
    }

    public void tearDown() {
        driver.quit();
    }

    @Scheduled(cron = "{job.update-zip-codes.expression}")
    public void execute() {
        driver.get("https://www.ctt.pt/fecas/login");
        driver.manage().window().setSize(new Dimension(1680, 952));
        driver.findElement(By.id("username")).sendKeys("nunalvesscp@hotmail.com");
        driver.findElement(By.id("password")).sendKeys("filipe123");
        driver.findElement(By.name("submit")).click();
        driver.get(
                "https://www.ctt.pt/feapl_2/app/restricted/postalCodeSearch/postalCodeDownloadFiles!downloadPostalCodeFile.jspx");

    }
}