package net.contextfw.web.application;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ResourceResponse {
    public void serve(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
