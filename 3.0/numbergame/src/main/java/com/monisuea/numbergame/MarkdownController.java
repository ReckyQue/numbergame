package com.monisuea.numbergame.controller;

import com.monisuea.numbergame.MarkdownUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class MarkdownController {

    @Autowired
    private MarkdownUtil markdownUtil;

    @GetMapping("/markdown/{file:.+}")
    public ResponseEntity<String> getMarkdownFile(@PathVariable String file) {
        try {
            String markdownContent = markdownUtil.loadMarkdownFile("static/markdown/" + file);
            String htmlContent = markdownUtil.parseMarkdownToHtml(markdownContent);
            return ResponseEntity.ok(htmlContent);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }
}