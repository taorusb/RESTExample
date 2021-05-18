package com.taorusb.restexample.controller;

import com.google.gson.Gson;
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
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = {"/user/*", "/file/*", "/event/*"})
public class DeleteControllerServlet extends HttpServlet {

    private UserServiceImpl userService;
    private FileServiceImpl fileService;
    private EventServiceImpl eventService;
    private Gson gson;
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
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        String idInfo = req.getPathInfo();
        String pathInfo = req.getRequestURI();
        if (idInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        String[] Id = idInfo.substring(1).split("/");
        String[] modelInfo = pathInfo.substring(1).split("/");
        if (Id.length > 1) {
            resp.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
        } else if (!checkId(Id[0])) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        Long id = Long.parseLong(Id[0]);
        switch (modelInfo[0]) {
            case "user":
                deleteUser(id);
                break;
            case "file":
                deleteFile(id);
                break;
            case "event":
                deleteEvent(id);
                break;
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

    private void deleteFile(Long id) {
        try {
            File file = fileService.getById(id);
            fileService.delete(id);
            sendOkForDelete(gson.toJson(file));
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteEvent(Long id) {
        try {
            Event event = eventService.getById(id);
            eventService.delete(id);
            sendOkForDelete(gson.toJson(event));
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private boolean checkId(String s) {
        if (!s.matches("\\d+")) {
            return false;
        }
        long id = Long.parseLong(s);
        if (id == 0 || id < 0) {
            return false;
        }
        return true;
    }

    private void sendOkForDelete(String s) {
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