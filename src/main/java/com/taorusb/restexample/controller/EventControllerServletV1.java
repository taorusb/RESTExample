package com.taorusb.restexample.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.service.EventService;
import com.taorusb.restexample.supplier.GsonSupplier;
import com.taorusb.restexample.supplier.ServiceSupplier;
import com.taorusb.restexample.supplier.SessionFactorySupplier;
import org.hibernate.ObjectNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Long.parseLong;

@WebServlet(urlPatterns = {"/events/*", "/event", "/event/*"})
public class EventControllerServletV1 extends HttpServlet {

    private EventService eventService;
    private Gson gson;
    private PrintWriter pw;
    private BufferedReader br;
    private HttpServletResponse resp;

    @Override
    public void init() throws ServletException {
        SessionFactorySupplier.getSessionFactory();
        ServiceSupplier serviceSupplier = ServiceSupplier.getInstance();
        eventService = serviceSupplier.getEventService();
        gson = GsonSupplier.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        String[] vals;
        long userId;
        long eventId;
        try {
            pw = resp.getWriter();
            String pathInfo = req.getPathInfo();
            vals = pathInfo.substring(1).split("/");
            if (vals.length == 1) {
                userId = parseLong(vals[0]);
                printEvent(0L, userId);
            } else {
                userId = parseLong(vals[0]);
                eventId = parseLong(vals[1]);
                printEvent(eventId, userId);
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
            saveEvent();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        try {
            br = req.getReader();
            updateEvent();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        long id = parseLong(req.getPathInfo().substring(1).split("/")[0]);
        deleteEvent(id);
    }

    private void printEvent(Long id, Long userId) {
        try {
            if (id == 0) {
                eventService.getByUserId(userId).forEach(event -> pw.println(gson.toJson(event)));
                sendOkForGet();
            } else {
                pw.println(gson.toJson(eventService.getByUserId(userId, id)));
                sendOkForGet();
            }
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
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

    private void idCheck(Long id) {
        if (id == 0 || id < 0) {
            throw new IllegalArgumentException("Id must not be negative or equals to zero.");
        }
    }
}