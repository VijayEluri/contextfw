package net.contextfw.remoting.fluent;

public interface RemotingConnection {
    
    Object invoke(Class<?> remotedInterface, ProxiedInvocation invocation);
}
