package com.tedu.springboot2306.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.tedu.springboot2306.entity.User;

@Controller
public class UserController {
    private static File userDir;

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
        File file = new File(userDir, username + ".obj");
        if (file.exists()) {
            User user =
        }

    }
}
