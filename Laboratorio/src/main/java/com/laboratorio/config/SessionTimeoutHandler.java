package com.laboratorio.config;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpSession;

@Component
@RequiredArgsConstructor
public class SessionTimeoutHandler {
    
    private static final int WARNING_TIME = 5 * 60 * 1000; 
    
    private final HttpSession session;
    
    public long getSessionTimeout() {
        return session.getMaxInactiveInterval() * 1000L; 
    }
    
    public long getTimeUntilExpiry() {
        long currentTime = System.currentTimeMillis();
        long lastAccessedTime = session.getLastAccessedTime();
        long maxInactiveInterval = session.getMaxInactiveInterval() * 1000L;
        
        return (lastAccessedTime + maxInactiveInterval) - currentTime;
    }
    
    public boolean shouldShowWarning() {
        long timeUntilExpiry = getTimeUntilExpiry();
        return timeUntilExpiry > 0 && timeUntilExpiry <= WARNING_TIME;
    }
    
    public int getWarningTimeInMinutes() {
        return WARNING_TIME / (60 * 1000);
    }
    
    public long getRemainingTimeInSeconds() {
        return getTimeUntilExpiry() / 1000;
    }
}