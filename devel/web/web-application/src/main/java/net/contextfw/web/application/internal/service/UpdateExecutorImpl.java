package net.contextfw.web.application.internal.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.contextfw.web.application.PageHandle;
import net.contextfw.web.application.WebApplicationException;
import net.contextfw.web.application.configuration.Configuration;
import net.contextfw.web.application.lifecycle.UpdateExecutor;
import net.contextfw.web.application.scope.WebApplicationStorage;
import net.contextfw.web.application.util.Tracker;

public class UpdateExecutorImpl extends AbstractHandler implements UpdateExecutor {

    private static final String CONTEXTFW_REFRESH = "contextfw-refresh";

    private static final String CONTEXTFW_UPDATE = "contextfw-update";

    private static final String CONTEXTFW_REMOVE = "contextfw-remove";
    
    private static final Pattern P = Pattern.compile("^p\\d+$");
    
    private final UpdateHandler handler;
    
    private final WebApplicationStorage storage;
    
    private final long maxInactivity;
    
    public UpdateExecutorImpl(UpdateHandler updateHandler,
            WebApplicationStorage storage,
            Configuration configuration) {
        super(configuration.get(Configuration.PROXIED));
        this.handler = updateHandler;
        this.storage = storage;
        this.maxInactivity = configuration.get(Configuration.MAX_INACTIVITY);
        Tracker.initialized(this);
    }
    
    @Override
    public void update(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) {
        try {
            final String[] uriSplits = request.getRequestURI().split("/");
            final int commandStart = getCommandStart(uriSplits);
            String remoteAddr = getRemoteAddr(request);
            if (commandStart != -1) {
                String command = uriSplits[commandStart];
                PageHandle handle = new PageHandle(uriSplits[commandStart + 1]);
   
                if (CONTEXTFW_REMOVE.equals(command)) {
                    storage.remove(handle, remoteAddr);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (CONTEXTFW_REFRESH.equals(command)) {
                    storage.refresh(handle, remoteAddr, System.currentTimeMillis() + maxInactivity);
                    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else if (CONTEXTFW_UPDATE.equals(command)) {
                    handler.update(handle, 
                           uriSplits[commandStart + 2],
                           uriSplits[commandStart + 3],
                           getMethodParameters(request),
                           remoteAddr,
                           new ServletResponder(response),
                           servlet,
                           request,
                           response);
                }
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }
    
    private List<String> getMethodParameters(HttpServletRequest request) {
        ArrayList<String> rv = new ArrayList<String>();
        
        @SuppressWarnings("unchecked")
        Enumeration<String> paramNames = request.getParameterNames();
        
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            
            if (P.matcher(name).matches()) {
                int pos = Integer.parseInt(name.substring(1));
                while (rv.size() <= pos) {
                    rv.add(null);
                }
                rv.set(Integer.parseInt(name.substring(1)), request.getParameter(name));
            }
        }
        return rv;
    }

    private int getCommandStart(String[] splits) {
        int remaining = splits.length;
        for (int i = 0; i < splits.length; i++) {
            remaining--;
            String s = splits[i];
            
            if ((CONTEXTFW_REMOVE.equals(s) || CONTEXTFW_REFRESH.equals(s)) && remaining >= 1) {
                return i;
            } else if (CONTEXTFW_UPDATE.equals(s) && remaining >= 3) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void update(PageHandle handle,
                       String componentId,
                       String method, 
                       List<String> params,
                       String remoteAddr,
                       PrintWriter writer) {
        try {
            handler.update(handle, 
                componentId,
                method,
                params,
                remoteAddr,
                new SimpleResponder(writer),
                null, null, null);
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public boolean update(PageHandle handle, 
                       Callable<Boolean> callable, 
                       String remoteAddr,
                       HttpServletResponse response) {

        try {
            return handler.update(handle, 
                callable,
                remoteAddr,
                new ServletResponder(response),
                null, null, response);
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public boolean update(PageHandle handle,
                       Callable<Boolean> callable, 
                       HttpServletRequest request,
                       HttpServletResponse response) {
        try {
            return handler.update(handle, 
                callable,
                getRemoteAddr(request),
                new ServletResponder(response),
                null, request, response);
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }

    @Override
    public boolean update(PageHandle handle, 
                       Callable<Boolean> callable, 
                       String remoteAddr, 
                       PrintWriter writer) {
        try {
            return handler.update(handle, 
                callable,
                remoteAddr,
                new SimpleResponder(writer),
                null, null, null);
        } catch (ServletException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
    }
}
