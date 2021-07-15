package com.eugene.watchcrawler.reader;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.file.Files.lines;
import static java.util.Objects.requireNonNull;

@Component
public class FileReader implements ReferenceNumbersReader {

    private static final String DELIMITER = "->";

    @SneakyThrows
    public List<String> readReferenceNumbers(final String fileName) {
        final Path path = Paths.get(requireNonNull(getClass().getClassLoader()
                .getResource(fileName)).toURI());

        try (final Stream<String> stream = lines(path, ISO_8859_1)) {
            return stream.map(line -> {
                final String[] split = line.split(DELIMITER);
                return split[split.length - 1];
            })
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }
}
