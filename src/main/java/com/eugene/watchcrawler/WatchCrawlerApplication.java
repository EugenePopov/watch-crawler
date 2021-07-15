package com.eugene.watchcrawler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@AllArgsConstructor
@SpringBootApplication
public class WatchCrawlerApplication implements CommandLineRunner {

    private final Crawler crawler;

    public static void main(String[] args) {
        SpringApplication.run(WatchCrawlerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        crawler.crawlData("pim-references.txt");
    }
}
