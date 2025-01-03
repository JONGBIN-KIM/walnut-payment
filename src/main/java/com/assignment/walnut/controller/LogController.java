package com.assignment.walnut.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@RestController
public class LogController {

    @GetMapping("/api/logs")
    public String getLog(@RequestParam String file) throws IOException {

        Path logFilePath = Paths.get(System.getProperty("user.dir"), "logs", file);

        if (!Files.exists(logFilePath)) {
            return "Log file not found: " + logFilePath.toString();
        }

        // 파일 읽기
        return Files.lines(logFilePath)
                .reduce("", (acc, line) -> acc + line + "\n");
    }
}
