package net.contextfw.remoting.fluent;

import java.util.HashMap;
import java.util.Map;

import net.contextfw.remoting.fluent.InvocationError.Type;

public class LocalServer implements RemotingConnection {

    private Map<Class<?>, Object> implementations = new HashMap<Class<?>, Object>();
    
    @Override
    public Object invoke(Class<?> remotedInterface, ProxiedInvocation invocation) {
        
        Object implementation = implementations.get(remotedInterface);
        
        if (implementation != null) {
            Object rv = invocation.invoke(implementation);
            if (rv instanceof InvocationError) {
                throw new RemotingException((InvocationError) rv);
            } else {
                return rv;
            }
        } 
        else {
            throw new RemotingException(new InvocationError(Type.NO_BINDING, remotedInterface.getName()));
        }
    }

    public <T> void bind(Class<T> remotedInterface, T implementation) {
        implementations.put(remotedInterface, implementation);
    }
}