package com.cloudweb.cloud.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class RootRedirectionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Redirect to /control/welcome-page for root URL requests
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();

        // Get the path of the current request
        String path = httpRequest.getRequestURI().substring(contextPath.length());

        // Check if it's the root URL ("/") and redirect
        if (path.isEmpty() || path.equals("/")) {
            httpResponse.sendRedirect(contextPath + "/control/welcome-page");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}

