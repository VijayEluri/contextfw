package net.contextfw.web.application.internal.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.internal.WebApplicationServletModule;
import net.contextfw.web.application.internal.service.DirectoryWatcher;
import net.contextfw.web.application.internal.service.InitHandler;
import net.contextfw.web.application.internal.service.ReloadingClassLoaderContext;

public class InitServlet extends HttpServlet {

    private List<Class<? extends Component>> chain; // NOSONAR
    
    private DirectoryWatcher watcher; // NOSONAR
    
    private ReloadingClassLoaderContext module; // NOSONAR
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        reloadClasses();
        handler.handleRequest(chain, this, req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        reloadClasses();
        handler.handleRequest(chain, this, req, resp);
    }

    private void reloadClasses() {
        if (watcher != null && watcher.hasChanged()) {
            module.reloadClasses();
        }
    }
    
    private static final long serialVersionUID = 1L;

    private final InitHandler handler;

    public InitServlet(ReloadingClassLoaderContext module,
                       DirectoryWatcher watcher,
                       InitHandler handler, 
                       List<Class<? extends Component>> chain) {
        this.handler = handler;
        this.chain = chain;
        this.watcher = watcher;
        this.module = module;
    }

    public void setChain(List<Class<? extends Component>> chain) {
        this.chain = chain;
    }
}
