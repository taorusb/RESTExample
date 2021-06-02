package com.taorusb.restexample.controller.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user/*", "/file/*", "/event/*"})
public class DeleteFilter implements Filter {

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
        if (httpMethod.equals("DELETE")) {
            String modelInfo = req.getRequestURI();
            checkURIForDelete(pathInfo, modelInfo);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
    }

    private void checkURIForDelete(String path, String modelType) throws ServletException, IOException {
        if (path.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String[] idVal = path.substring(1).split("/");
        String model = modelType.substring(1).split("/")[0];
        if (idVal.length > 1) {
            resp.setStatus(HttpServletResponse.SC_REQUEST_URI_TOO_LONG);
            return;
        } else if (!checkId(idVal[0])) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String URI = model + "/" + idVal[0];
        switch (model) {
            case "user":
                forwardRequest(URI);
                break;
            case "file":
                forwardRequest(URI);
                break;
            case "event":
                forwardRequest(URI);
                break;
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