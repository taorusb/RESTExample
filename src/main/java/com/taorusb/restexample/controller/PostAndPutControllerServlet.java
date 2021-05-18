package com.taorusb.restexample.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taorusb.restexample.config.GsonSupplier;
import com.taorusb.restexample.config.ServiceSupplier;
import com.taorusb.restexample.config.SessionFactorySupplier;
import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.service.impl.EventServiceImpl;
import com.taorusb.restexample.service.impl.FileServiceImpl;
import com.taorusb.restexample.service.impl.UserServiceImpl;
import org.hibernate.ObjectNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/user", "/file", "/event"})
public class PostAndPutControllerServlet extends HttpServlet {

    private UserServiceImpl userService;
    private FileServiceImpl fileService;
    private EventServiceImpl eventService;
    private Gson gson;
    private BufferedReader br;
    private HttpServletResponse resp;

    @Override
    public void init() throws ServletException {
        SessionFactorySupplier.getSessionFactory();
        ServiceSupplier serviceSupplier = ServiceSupplier.getInstance();
        userService = serviceSupplier.getUserService();
        fileService = serviceSupplier.getFileService();
        eventService = serviceSupplier.getEventService();
        gson = GsonSupplier.getGson();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        br = req.getReader();
        String uriInfo = req.getRequestURI().substring(1).split("/")[0];
        switch (uriInfo) {
            case "user":
                saveUser();
                break;
            case "file":
                saveFile();
                break;
            case "event":
                saveEvent();
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        br = req.getReader();
        String uriInfo = req.getRequestURI().substring(1).split("/")[0];
        switch (uriInfo) {
            case "user":
                updateUser();
                break;
            case "file":
                updateFile();
                break;
            case "event":
                updateEvent();
                break;
        }
    }

    private void updateUser() {
        try {
            String json = br.readLine();
            User user = gson.fromJson(json, User.class);
            idCheck(user.getId());
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

    private void updateFile() {
        try {
            String json = br.readLine();
            File file = gson.fromJson(json, File.class);
            idCheck(file.getId());
            idCheck(file.getUser().getId());
            fileService.update(file);
            sendOkForPut(json);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void updateEvent() {
        try {
            String json = br.readLine();
            Event event = gson.fromJson(json, Event.class);
            idCheck(event.getId());
            idCheck(event.getUser().getId());
            idCheck(event.getFile().getId());
            eventService.update(event);
            sendOkForPut(json);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

    private void saveFile() {
        try {
            File file = gson.fromJson(br, File.class);
            fileService.save(file);
            sendOkForPost(gson.toJson(file));
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void saveEvent() {
        try {
            Event event = gson.fromJson(br, Event.class);
            eventService.save(event);
            sendOkForPost(gson.toJson(event));
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (JsonSyntaxException | IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void idCheck(Long id) {
        if (id == 0 || id < 0) {
            throw new IllegalArgumentException("Id must not be negative or equals to zero.");
        }
    }

    private void sendOkForPost(String s) {
        PrintWriter pw;
        try {
            pw = resp.getWriter();
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            return;
        }
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_CREATED);
        pw.write(s);
    }

    private void sendOkForPut(String s) {
        PrintWriter pw;
        try {
            pw = resp.getWriter();
        } catch (IOException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            return;
        }
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        pw.write(s);
    }
}