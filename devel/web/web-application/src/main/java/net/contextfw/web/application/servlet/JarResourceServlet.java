package net.contextfw.web.application.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;

import net.contextfw.web.service.application.WebApplicationException;

public abstract class JarResourceServlet extends ResourceServlet {

    private static final long serialVersionUID = 1L;

    List<String> rootPaths = new ArrayList<String>();

    public List<File> getRoots() {
        try {
            List<File> roots = new ArrayList<File>();
            for (String rootPath : rootPaths) {
                Enumeration<URL> resources = this.getClass().getClassLoader().getResources(rootPath);
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    File file = new File(URLDecoder.decode(resource.getFile(), "UTF-8"));
                    if (file.isDirectory()) {
                        roots.add(file);
                    }
                }
            }
            return roots;
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    public void init() throws ServletException {
        for (String rootPackage : getRootPackages()) {
            rootPaths.add(toPath(rootPackage));
        }
    }
    
    private String toPath(String pckg) {
        return pckg.trim().replaceAll("\\.", "/");
    }
    
    public abstract List<String> getRootPackages();
}