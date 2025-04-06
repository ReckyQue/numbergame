package com.monisuea.numbergame;

import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class MarkdownUtil {

    /**
     * 从类路径加载Markdown文件内容
     * @param filePath 文件路径
     * @return Markdown文件内容
     * @throws IOException 如果文件无法加载
     */
    public String loadMarkdownFile(String filePath) throws IOException {
        Resource resource = new ClassPathResource(filePath);
        if (resource.exists()) {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IOException("Markdown file not found: " + filePath);
        }
    }

    /**
     * 将Markdown内容解析为HTML
     * @param markdown Markdown内容
     * @return 解析后的HTML内容
     */
    public String parseMarkdownToHtml(String markdown) {
        // 解析表格
        Iterable<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();

        return renderer.render(document);
    }
}