package com.tedu.springboot2306.controller;

import com.tedu.springboot2306.entity.Article;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@Controller
public class ArticleController {
    private static File articleDir;

    static {
        articleDir = new File("./articledir");
        if (!articleDir.exists()) {
            articleDir.mkdirs();
        }
    }

    @RequestMapping("/writeArticle")
    public void writeArticle(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String content = request.getParameter("content");
        Article article = new Article(title, author, content);
        File file = new File(articleDir, title + ".obj");

        if (title == null || title.isEmpty() ||
        author == null || author.isEmpty() ||
        content == null || content.isEmpty()) {
            try {
                response.sendRedirect("/article_info_error.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (file.exists()) {
            try {
                response.sendRedirect("/have_article.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(article);
            response.sendRedirect("/submit_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
