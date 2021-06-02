package com.taorusb.restexample.controller.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.taorusb.restexample.controller.Keys.*;

@WebFilter(urlPatterns = {"/users/*"})
public class GetFilter implements Filter {

    private HttpServletRequest req;
    private HttpServletResponse resp;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        req = (HttpServletRequest) servletRequest;
        resp = (HttpServletResponse) servletResponse;
        String httpMethod = req.getMethod();
        String pathInfo = req.getPathInfo();
        if (httpMethod.equals("GET")) {
            if (pathInfo == null) {
                forwardRequest("/users");
                return;
            }
            checkURI(pathInfo.substring(1).split("/"));
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    private void checkURI(String[] URIkeys) {
        String URI;
        try {
            if (URIkeys.length == URI_USER_ID.getReqNum()) {
                if (checkId(URIkeys[0])) {
                    URI = "/users/" + URIkeys[0];
                    forwardRequest(URI);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if (URIkeys.length == URI_COLLECTIONS.getReqNum()) {
                if (URIkeys[1].equals("files") && checkId(URIkeys[0])) {
                    URI = "/files/" + URIkeys[0];
                    forwardRequest(URI);
                } else if (URIkeys[1].equals("events") && checkId(URIkeys[0])) {
                    URI = "/events/" + URIkeys[0];
                    forwardRequest(URI);
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if (URIkeys.length == URI_COLLECTIONS_WITH_ID.getReqNum()) {
                if (URIkeys[1].equals("files")) {
                    if (checkId(URIkeys[0]) && checkId(URIkeys[2])) {
                        URI = "/files/" + URIkeys[0] + "/" + URIkeys[2];
                        forwardRequest(URI);
                    } else  {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else if (URIkeys[1].equals("events")) {
                    if (checkId(URIkeys[0]) && checkId(URIkeys[2])) {
                        URI = "/events/" + URIkeys[0] + "/" + URIkeys[2];
                        forwardRequest(URI);
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

    private void forwardRequest(String s) throws ServletException, IOException {
        req.getServletContext()
                .getRequestDispatcher(s)
                .forward(req, resp);
    }
}