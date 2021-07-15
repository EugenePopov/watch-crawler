package com.eugene.watchcrawler;

import com.eugene.watchcrawler.reader.ReferenceNumbersReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class Crawler {

    private final ReferenceNumbersReader referenceNumbersReader;

    public void crawlData(final String fileName) {
        referenceNumbersReader.readReferenceNumbers(fileName).forEach(log::info);
    }
}
