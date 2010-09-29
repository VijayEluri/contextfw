package net.contextfw.remoting.fluent;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.io.AbstractSerializerFactory;

public class HessianClient implements RemotingConnection {

    private RemotingConnection serverConnection;
    
    @Override
    public Object invoke(Class<?> remotedInterface, ProxiedInvocation invocation) {
        Object rv = serverConnection.invoke(remotedInterface, invocation);
        if (rv instanceof InvocationError) {
            throw new RemotingException((InvocationError) rv);
        } else {
            return rv;
        }
    }
    
    public HessianClient(String url) throws MalformedURLException {
        this(url, null);
    }
    
    public HessianClient(String url, AbstractSerializerFactory customSerializerFactory) throws MalformedURLException {
        
        HessianProxyFactory factory = new HessianProxyFactory();
        if (customSerializerFactory != null) {
            factory.getSerializerFactory().addFactory(customSerializerFactory);
        }
        serverConnection = (RemotingConnection) factory.create(RemotingConnection.class, url);
    }
}