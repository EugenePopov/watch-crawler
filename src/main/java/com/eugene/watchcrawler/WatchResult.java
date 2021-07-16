package com.eugene.watchcrawler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class WatchResult {
    private String referenceNumber;
    private List<String> ids;
}
