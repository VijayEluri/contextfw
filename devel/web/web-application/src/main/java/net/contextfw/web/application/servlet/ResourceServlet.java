package net.contextfw.web.application.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.internal.util.ResourceScanner;

public abstract class ResourceServlet extends HttpServlet {

    private static final long serialVersionUID = -1979474932427776224L;

    private volatile String content = null;

    private long capacity = 0;

    public void clean() {
        content = null;
        capacity = 0;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (clear()) {
            clean();
        }

        if (content == null) {
            synchronized (this) {
                if (content == null) {
                    StringBuilder contentBuilder = new StringBuilder();
                    List<File> files = ResourceScanner.findResources(getRootPaths(), getAcceptor());
                    for (File file : files) {
                        addContent(contentBuilder, file);
                    }
                    content = contentBuilder.toString();
                }
            }
        }

        resp.getWriter().print(content);
        resp.getWriter().close();
    }

    private void addContent(StringBuilder contentBuilder, File file) {
        String line = null;
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));

            capacity = capacity + file.length();
            contentBuilder.ensureCapacity((int) capacity);

            while ((line = r.readLine()) != null) {
                contentBuilder.append(line);
                contentBuilder.append("\n");
            }
            r.close();
        } catch (FileNotFoundException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    public abstract boolean clear();

    protected abstract Pattern getAcceptor();

    protected abstract List<String> getRootPaths();
}