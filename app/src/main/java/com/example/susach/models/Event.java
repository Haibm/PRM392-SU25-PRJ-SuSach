package com.example.susach.model;

public class Event {
    private String id;
    private String name;
    private String contents;
    private String description;
    private int startDate;
    private int endDate;
    private String imageUrl;
    private String eraId;

    // Constructor rỗng cho Firestore
    public Event() {}

    public Event(String id, String name, String contents, String description, int startDate, int endDate, String imageUrl, String eraId) {
        this.id = id;
        this.name = name;
        this.contents = contents;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.eraId = eraId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStartDate() { return startDate; }
    public void setStartDate(int startDate) { this.startDate = startDate; }

    public int getEndDate() { return endDate; }
    public void setEndDate(int endDate) { this.endDate = endDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getEraId() { return eraId; }
    public void setEraId(String eraId) { this.eraId = eraId; }
} 