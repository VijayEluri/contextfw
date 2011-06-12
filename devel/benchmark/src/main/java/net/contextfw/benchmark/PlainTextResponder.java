package net.contextfw.benchmark;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.remote.ResourceResponse;

public class PlainTextResponder implements ResourceResponse {

    private final String text;
    
    public PlainTextResponder(String text) {
        this.text = text;
    }

    @Override
    public void serve(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().write(text);
    }

}
