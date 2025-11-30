package com.laboratorio.model;

import lombok.Data;

@Data
public class SessionStatusDTO {
    private boolean active;
    private long timeUntilExpiry;
    private boolean showWarning;
    private int warningTime;
    private long remainingTimeInSeconds;
    
    public SessionStatusDTO(boolean active, long timeUntilExpiry, boolean showWarning, int warningTime, long remainingTimeInSeconds) {
        this.active = active;
        this.timeUntilExpiry = timeUntilExpiry;
        this.showWarning = showWarning;
        this.warningTime = warningTime;
        this.remainingTimeInSeconds = remainingTimeInSeconds;
    }
}