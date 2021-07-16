package com.eugene.watchcrawler;

import com.eugene.watchcrawler.reader.ReferenceNumbersReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Component
public class Crawler implements DisposableBean {

    private final ReferenceNumbersReader referenceNumbersReader;
    private final WebDriver driver;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern PATTERN = Pattern.compile("(?<=--id)(.*)(?=.htm)");

    public Crawler(final ReferenceNumbersReader referenceNumbersReader) {
        this.referenceNumbersReader = referenceNumbersReader;
        System.setProperty("webdriver.chrome.driver", ".\\chromedriver.exe");
        final ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        final WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(5, SECONDS);
        this.driver = driver;
    }

    @SneakyThrows
    public void crawlData(final String fileName) {
        final List<WatchResult> watches = referenceNumbersReader.readReferenceNumbers(fileName).stream()
                .map(referenceNumber -> new WatchResult(referenceNumber, findWatches(referenceNumber)))
                .filter(r -> isNotEmpty(r.getIds()))
                .collect(toList());
        objectMapper.writeValue(new File("D:\\dev\\projects\\watch-crawler\\watches.json"), watches);
    }

    @SneakyThrows
    private List<String> findWatches(final String referenceNumber) {
        log.info("Searching for: {}", referenceNumber);
        try {
            final String url = "https://www.chrono24.com/search/index.htm?query=%s&dosearch=true&searchexplain=1&watchTypes=&accessoryTypes=&usedOrNew=new";
            driver.get(String.format(url, referenceNumber));
            try {
                final WebElement cookiesForm = driver.findElement(By.id("cboxContent"));

                final WebElement cookies = cookiesForm.findElement(By.tagName("a"));
                cookies.click();
            } catch (Exception e) {
            }

            final WebElement resultContainer = driver.findElement(By.cssSelector("#wt-watches"));
            Thread.sleep(500);
            final List<WebElement> items = resultContainer.findElements(By.xpath("./child::*"));

            return extractIds(items);
        } catch (final Exception e) {
            log.error("Exception processing watch: {}", referenceNumber);
            log.error("Exception details:", e);
            return Collections.emptyList();
        }
    }

    private List<String> extractIds(final List<WebElement> items) {
        final List<String> hrefs = items.stream()
                .filter(i -> i.getAttribute("class").equals("article-item-container wt-search-result"))
                .map(i -> i.findElement(By.tagName("a")).getAttribute("href"))
                .filter(Objects::nonNull)
                .collect(toList());

        return hrefs.stream()
                .map(PATTERN::matcher)
                .filter(Matcher::find)
                .map(m -> m.group(1))
                .collect(toList());
    }

    @Override
    public void destroy() {
        driver.quit();
    }
}
