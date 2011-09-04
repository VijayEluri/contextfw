package net.contextfw.web.commons.js;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Singleton;

@Singleton
class JsTemplateServlet extends HttpServlet {

    private static final long serialVersionUID = -1979474932427776224L;
    private volatile String content = null;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/javascript");
        resp.getWriter().append(content);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
