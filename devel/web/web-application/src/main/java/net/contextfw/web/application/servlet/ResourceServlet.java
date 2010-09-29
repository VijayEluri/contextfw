package net.contextfw.web.application.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = -1979474932427776224L;

    private volatile String content = null;

    private long capacity = 0;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (clear()) {
            content = null;
            capacity = 0;
        }

        if (content == null) {
            synchronized (this) {
                if (content == null) {
                    StringBuilder contentBuilder = new StringBuilder();
                    for (File file : getRoots()) {
                        addContent(contentBuilder, file);
                    }
                    content = contentBuilder.toString();
                }
            }
        }

        resp.getWriter().print(content);
        resp.getWriter().close();
    }

    private void addContent(StringBuilder contentBuilder, File root) {

        File[] files = root.listFiles();

        try {
            for (File file : files) {
                if (file.isDirectory()) {
                    addContent(contentBuilder, file);
                }
                else if (accept(file)) {
                    String line = null;
                    BufferedReader r = new BufferedReader(new FileReader(file));

                    capacity = capacity + file.length();
                    contentBuilder.ensureCapacity((int) capacity);

                    while ((line = r.readLine()) != null) {
                        contentBuilder.append(line);
                        contentBuilder.append("\n");
                    }
                    r.close();
                }
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public abstract boolean clear();

    public abstract boolean accept(File file);

    public abstract List<File> getRoots();
}