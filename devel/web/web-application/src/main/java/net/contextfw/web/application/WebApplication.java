package net.contextfw.web.application;

public interface WebApplication {
    
    boolean isExpired(long now);
    
    int refresh(long expires);
    
    String getRemoteAddr();
}
