package com.rmb938.mn2.docker.servlets.api;

import com.rmb938.mn2.docker.DatabaseResource;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Log4j2
public class APIServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        DatabaseResource.initDatabase();
        log.info("Init "+this.getServletName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(getJSON(req, resp).toString());
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(putJSON(req, resp).toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(postJSON(req, resp).toString());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/json");
        PrintWriter printWriter = resp.getWriter();
        printWriter.println(deleteJSON(req, resp).toString());
    }

    public JSONObject getJSON(HttpServletRequest req, HttpServletResponse resp) {
        return new JSONObject();
    }

    public JSONObject postJSON(HttpServletRequest req, HttpServletResponse resp) {
        return new JSONObject();
    }

    public JSONObject putJSON(HttpServletRequest req, HttpServletResponse resp) {
        return new JSONObject();
    }

    public JSONObject deleteJSON(HttpServletRequest req, HttpServletResponse resp) {
        return new JSONObject();
    }
}
