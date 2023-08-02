package com.tedu.springboot2306.controller;

import com.tedu.springboot2306.entity.Article;
import com.tedu.springboot2306.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ArticleController {
    private static File articleDir;
    private List<Article> articleList = new ArrayList<>();

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

    @RequestMapping("/articlelist")
    public void articleList(HttpServletRequest request, HttpServletResponse response) {
        articleList.clear();
        File[] files = articleDir.listFiles(file -> file.getName().endsWith(".obj"));
        for (File file: files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));) {
                articleList.add((Article) ois.readObject());
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(articleList);
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter pw = response.getWriter();
            pw.println("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>" +
                    "<meta charset=\"utf-8\">" +
                    "</head>" +
                    "<body>" +
                    "<center>" +
                    "<h1>文章列表</h1>" +
                    "<table border=\"1\">");
            pw.println(
                    "<tr>" +
                    "<td>标题</td>" +
                    "<td>作者</td>" +
                    "</tr>");
            for (Article article: articleList) {
                pw.println(
                        "<tr>" +
                        "<td><a href=\"/showArticle?title=" + article.getTitle() + "\">" + article.getTitle() + "</a></td>" +
                        "<td>" + article.getAuthor() + "</td>" +
                        "</tr>");
            }
            pw.println("</table>" +
                    "<a href=\"/index.html\">返回首页</a>" +
                    "</center></body>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/showArticle")
    public void showArticle(HttpServletRequest request, HttpServletResponse response) {
        String title = request.getParameter("title");
        File file = new File(articleDir, title + ".obj");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));) {
                Article article = (Article) ois.readObject();
                String articlePage = "<html>" +
                        "<head>" +
                        "<meta charset=\"utf-8\">" +
                        "</head>" +
                        "<body>" +
                        "<center>" +
                        "<table border=\"1\">" +
                        "<tr>" +
                        "<td>标题</td>" +
                        "<td>" + title + "</td>" +
                        "</tr>" +
                        "<tr>" +
                        "<td>作者</td>" +
                        "<td>" + article.getAuthor() + "</td>" +
                        "</tr>" +
                        "<tr><td align=\"center\" colspan=\"2\">" + article.getContent() + "</td></tr>" +
                        "</table>" +
                        "<a href=\"/index.html\">回到首页</a>" +
                        "</center>" +
                        "</body>" +
                        "</html>";
                response.setContentType("text/html;charset=utf-8");
                PrintWriter pw = response.getWriter();
                pw.println(articlePage);
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

}
