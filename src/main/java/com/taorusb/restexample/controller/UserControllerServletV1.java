package com.taorusb.restexample.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taorusb.restexample.supplier.GsonSupplier;
import com.taorusb.restexample.supplier.ServiceSupplier;
import com.taorusb.restexample.supplier.SessionFactorySupplier;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.service.UserService;
import org.hibernate.ObjectNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/users/*", "/user", "/user/*"})
public class UserControllerServletV1 extends HttpServlet {

    private UserService userService;
    private Gson gson;
    private PrintWriter pw;
    private BufferedReader br;
    private HttpServletResponse resp;

    @Override
    public void init() throws ServletException {
        SessionFactorySupplier.getSessionFactory();
        ServiceSupplier serviceSupplier = ServiceSupplier.getInstance();
        userService = serviceSupplier.getUserService();
        gson = GsonSupplier.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        try {
            pw = resp.getWriter();
            String pathInfo = req.getPathInfo();
            if (pathInfo == null) {
                userService.getAll().forEach(user -> pw.println(gson.toJson(user)));
                sendOkForGet();
            } else {
                pathCheck(pathInfo, "GET");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        try {
            br = req.getReader();
            saveUser();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        try {
            br = req.getReader();
            updateUser();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        String idInfo = req.getPathInfo();
        pathCheck(idInfo, "DELETE");
    }

    private void printUser(Long id) {
        try {
            User user = userService.getById(id);
            pw.println(gson.toJson(user));
            sendOkForGet();
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void saveUser() {
        try {
            String json = br.readLine();
            User user = gson.fromJson(json, User.class);
            userService.save(user);
            sendOkForPost(gson.toJson(user));
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void updateUser() {
        try {
            String json = br.readLine();
            User user = gson.fromJson(json, User.class);
            userService.update(user);
            sendOkForPut(json);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteUser(Long id) {
        try {
            User user = userService.getById(id);
            userService.delete(id);
            sendOkForDelete(gson.toJson(user));
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendOkForGet() {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendOkForPost(String s) throws IOException {
        pw = resp.getWriter();
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        pw.write(s);
    }

    private void sendOkForPut(String s) throws IOException {
        pw = resp.getWriter();
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        pw.write(s);
    }

    private void sendOkForDelete(String s) throws IOException {
        pw = resp.getWriter();
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        pw.write(s);
    }

    private void pathCheck(String s, String method) {
        String[] strings = s.substring(1).split("/");
        Long id = Long.parseLong(strings[0]);
        switch (method) {
            case "GET":
                printUser(id);
                break;
            case "DELETE":
                deleteUser(id);
                break;
        }
    }
}