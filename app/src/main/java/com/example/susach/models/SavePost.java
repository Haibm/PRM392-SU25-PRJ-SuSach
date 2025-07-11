package com.example.susach.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SavePost {
    private String eventId;
    private String nameEvent;
    private Date savedAt;
    private String userId;

    public SavePost() {}

    public SavePost(String eventId, String nameEvent, Date savedAt, String userId) {
        this.eventId = eventId;
        this.nameEvent = nameEvent;
        this.savedAt = savedAt;
        this.userId = userId;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getNameEvent() { return nameEvent; }
    public void setNameEvent(String nameEvent) { this.nameEvent = nameEvent; }

    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", eventId);
        map.put("nameEvent", nameEvent);
        map.put("savedAt", savedAt);
        map.put("userId", userId);
        return map;
    }
} 