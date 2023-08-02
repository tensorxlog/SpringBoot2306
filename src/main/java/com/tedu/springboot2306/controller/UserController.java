package com.tedu.springboot2306.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.tedu.springboot2306.entity.User;

@Controller
public class UserController {
    private static File userDir;
    private List<User> userList = new ArrayList<>();

    static {
        userDir = new File("./userdir");
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
    }

    @RequestMapping("/regUser")
    public void reg(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理用户注册");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String ageStr = request.getParameter("age");
        System.out.println("username = " + username + ", password  = " + password + ", nickname = " + nickname + ", age = " + ageStr);

        if (username == null || username.isEmpty() ||
        password == null || password.isEmpty() ||
        nickname == null || nickname.isEmpty() ||
        ageStr == null || !ageStr.matches("[0-9]+")) {
            try {
                response.sendRedirect("reg_info_error.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        User user = new User(username, password, nickname, Integer.parseInt(ageStr));
        File file = new File(userDir, username + ".obj");
        if (file.exists()) {
            try {
                response.sendRedirect("/have_user.html");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(user);
            response.sendRedirect("./reg_success.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/loginUser")
    public void login(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("开始处理用户登录");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || username.isEmpty() ||
        password == null || password.isEmpty()) {
            try {
                response.sendRedirect("login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        File file = new File(userDir, username + ".obj");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));) {
                User user = (User) ois.readObject();
                String savedPassword = user.getPassword();
                if (password.equals(savedPassword)) {
                    response.sendRedirect("/login_success.html");
                } else {
                    response.sendRedirect("/login_fail.html");
                }
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                response.sendRedirect("login_info_error.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping("/userlist")
    public void userList(HttpServletRequest request, HttpServletResponse response) {
        userList.clear();
        File[] files = userDir.listFiles(file -> file.getName().endsWith(".obj"));
        for (File file: files) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                userList.add((User) ois.readObject());
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

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
                    "<h1>用户列表</h1>" +
                    "<table border=\"1\">");
            pw.println(
                    "<tr>" +
                    "<td>用户名</td>" +
                    "<td>密码</td>" +
                    "<td>昵称</td>" +
                    "<td>年龄</td>" +
                    "</tr>");
            for (User user: userList) {
                pw.println(
                    "<tr>" +
                    "<td>" + user.getUsername() + "</td>" +
                    "<td>" + user.getPassword() + "</td>" +
                    "<td>" + user.getNickname() + "</td>" +
                    "<td>" + user.getAge() + "</td>" +
                    "</tr>");
            }
            pw.println("</table>" +
                    "<a href=\"/index.html\">返回首页</a>" +
                    "</center></body>");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(userList);
    }
}
