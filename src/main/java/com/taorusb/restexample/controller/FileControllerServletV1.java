package com.taorusb.restexample.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.service.FileService;
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

@WebServlet(urlPatterns = {"/files/*", "/file", "/file/*"})
public class FileControllerServletV1 extends HttpServlet {

    private FileService fileService;
    private Gson gson;
    private PrintWriter pw;
    private BufferedReader br;
    private HttpServletResponse resp;

    @Override
    public void init() throws ServletException {
        SessionFactorySupplier.getSessionFactory();
        ServiceSupplier serviceSupplier = ServiceSupplier.getInstance();
        fileService = serviceSupplier.getFileService();
        gson = GsonSupplier.getGson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.resp = resp;
        String[] vals;
        long userId;
        long fileId;
        try {
            pw = resp.getWriter();
            String pathInfo = req.getPathInfo();
            vals = pathInfo.substring(1).split("/");
            if (vals.length == 1) {
                userId = parseLong(vals[0]);
                printFile(0L, userId);
            } else {
                userId = parseLong(vals[0]);
                fileId = parseLong(vals[1]);
                printFile(fileId, userId);
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
            saveFile();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        try {
            br = req.getReader();
            updateFile();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.resp = resp;
        long id = parseLong(req.getPathInfo().substring(1).split("/")[0]);
        deleteFile(id);
    }

    private void printFile(Long id, Long userId) {
        try {
            if (id == 0) {
                fileService.getByUserId(userId).forEach(file -> pw.println(gson.toJson(file)));
                sendOkForGet();
            } else {
                pw.println(gson.toJson(fileService.getByUserId(userId, id)));
                sendOkForGet();
            }
        } catch (ObjectNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
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