package com.taorusb.restexample.controller;

import com.google.gson.Gson;
import com.taorusb.restexample.config.GsonSupplier;
import com.taorusb.restexample.config.ServiceSupplier;
import com.taorusb.restexample.config.SessionFactorySupplier;
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

import static com.taorusb.restexample.controller.Keys.*;
import static java.lang.Long.*;

@WebServlet(value = "/users/*")
public class GetControllerServlet extends HttpServlet {

    private UserServiceImpl userService;
    private FileServiceImpl fileService;
    private EventServiceImpl eventService;
    private Gson gson;
    private PrintWriter printWriter;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        printWriter = resp.getWriter();
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            userService.getAll().forEach(user -> printWriter.println(gson.toJson(user)));
            sendOk();
            return;
        }
        checkString(pathInfo.substring(1).split("/"));
    }

    private void checkString(String[] URIkeys) {
        try {
            if (URIkeys.length == URI_USER_ID.getReqNum()) {
                if (checkId(URIkeys[0])) {
                    printUser(parseLong(URIkeys[0]));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if (URIkeys.length == URI_COLLECTIONS.getReqNum()) {
                if (URIkeys[1].equals("files") && checkId(URIkeys[0])) {
                    printFile(0L, parseLong(URIkeys[0]));
                } else if (URIkeys[1].equals("events") && checkId(URIkeys[0])) {
                    printEvent(0L, parseLong(URIkeys[0]));
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if (URIkeys.length == URI_COLLECTIONS_WITH_ID.getReqNum()) {
                if (URIkeys[1].equals("files")) {
                    if (checkId(URIkeys[0]) && checkId(URIkeys[2])) {
                        printFile(parseLong(URIkeys[2]), parseLong(URIkeys[0]));
                    } else  {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else if (URIkeys[1].equals("events")) {
                    if (checkId(URIkeys[0]) && checkId(URIkeys[2])) {
                        printEvent(parseLong(URIkeys[2]), parseLong(URIkeys[0]));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void printUser(Long id) {
        try {
            if (id == 0) {
                userService.getAll().forEach(user -> printWriter.println(gson.toJson(user)));
                sendOk();
            } else {
                printWriter.println(gson.toJson(userService.getById(id)));
                sendOk();
            }
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void printFile(Long id, Long userId) {
        try {
            if (id == 0) {
                fileService.getByUserId(userId).forEach(file -> printWriter.println(gson.toJson(file)));
                sendOk();
            } else {
                printWriter.println(gson.toJson(fileService.getByDoubleId(userId, id)));
                sendOk();
            }
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void printEvent(Long id, Long userId) {
        try {
            if (id == 0) {
                eventService.getByUserId(userId).forEach(event -> printWriter.println(gson.toJson(event)));
                sendOk();
            } else {
                printWriter.println(gson.toJson(eventService.getByDoubleId(userId, id)));
                sendOk();
            }
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

    private void sendOk() {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}