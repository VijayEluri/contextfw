package net.contextfw.web.application.lifecycle;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestInvocation {

    void invoke(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
