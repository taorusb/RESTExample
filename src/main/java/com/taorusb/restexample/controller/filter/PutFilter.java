package com.taorusb.restexample.controller.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/user", "/file", "/event"})
public class PutFilter implements Filter {

    private HttpServletRequest req;
    private HttpServletResponse resp;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        req = (HttpServletRequest) servletRequest;
        resp = (HttpServletResponse) servletResponse;
        String URL = req.getServletPath().substring(1).split("/")[0];
        String httpMethod = req.getMethod();
        if (httpMethod.equals("PUT")) {
            switch (URL) {
                case "user":
                    forwardRequest("/user");
                    break;
                case "file":
                    forwardRequest("/file");
                    break;
                case "event":
                    forwardRequest("/event");
                    break;
            }
        }
    }

    @Override
    public void destroy() {
    }

    private void forwardRequest(String s) throws ServletException, IOException {
        req.getServletContext()
                .getRequestDispatcher(s)
                .forward(req, resp);
    }
}