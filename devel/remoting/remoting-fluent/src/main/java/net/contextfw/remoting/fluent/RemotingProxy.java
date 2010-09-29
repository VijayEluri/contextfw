package net.contextfw.remoting.fluent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Stack;

public final class RemotingProxy implements InvocationHandler {

    private final Class<?> remotedInterface;
    private final RemotingConnection connection;
    private ProxiedInvocation previous;
    private ProxiedInvocation first;
    private final boolean isFirst;
    
    
    private RemotingProxy(boolean isFirst, RemotingConnection connection, Class<?> remotedInterface, ProxiedInvocation first,
            ProxiedInvocation previous) {
        this.connection = connection;
        this.remotedInterface = remotedInterface;
        this.first = first;
        this.previous = previous;
        this.isFirst = isFirst;
    }

    @SuppressWarnings("unchecked")
    private static <T> T createProxy(boolean isFirst, RemotingConnection connection, Class<T> remotedInterface,
            ProxiedInvocation first, ProxiedInvocation previous) {
        return (T) Proxy.newProxyInstance(remotedInterface.getClassLoader(), new Class[] { remotedInterface },
                new RemotingProxy(isFirst, connection, remotedInterface, first, previous));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] parameters) throws Throwable {
        
        Class<?> returnType = method.getReturnType();

        String methodName = method.getName();

        InvocationError error = ProxiedInvocation.getNonAllowedMethodError(methodName);

        if (error == null) {

            ProxiedInvocation current = new ProxiedInvocation(remotedInterface, 
                    method.getName(), method.getParameterTypes(), parameters);

            if (isFirst) {
                first = current;
                previous = current;
            }
            else {
                previous.setNextInvocation(current);
            }

            if (returnType != null && isRemoted(returnType)) {
                return createProxy(false, connection, returnType, first, current);
            } else {
                return first.invokeRemoted(connection);
            }
        }
        else {
            throw new RemotingException(error);
        }
    }

    private boolean isRemoted(Class<?> type) {

        if (!type.isInterface()) {
            return false;
        }

        Stack<Class<?>> stack = new Stack<Class<?>>();
        stack.add(type);
        while (!stack.isEmpty()) {
            Class<?> current = stack.pop();
            if (current.isAnnotationPresent(Remoted.class)) {
                return true;
            }
            else {
                for (Class<?> superType : current.getInterfaces()) {
                    stack.push(superType);
                }
            }
        }
        return false;
    }

    public static <T> T createProxy(Class<T> remotedInterface, RemotingConnection connection) {
        return RemotingProxy.createProxy(true, connection, remotedInterface, null, null);
    }
}