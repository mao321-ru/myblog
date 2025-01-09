package org.example.mbg;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

public class AppServlet extends HttpServlet {

    private final static Logger log = Logger.getLogger(org.example.mbg.AppServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Date date = new Date();
        log.info( "request received: date: " + date.toString());
        req.setAttribute("generatedTime", date.toString());
        req.getRequestDispatcher("WEB-INF/views/index.jsp").forward(req, resp);
    }
}
