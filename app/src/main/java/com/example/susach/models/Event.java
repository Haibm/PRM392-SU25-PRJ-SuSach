package com.example.susach.models;

public class Event {
    private String id;
    private String name;
    private String description;
    private String contents;
    private int startDate;
    private int endDate;
    private String imageUrl;
    private String eraId;
    private String summary;
    private String imageContent;
    private String createBy;
    private String updateBy;

    // Constructor rỗng cho Firestore
    public Event() {}

    public Event(String id, String name, String description, String contents, int startDate, int endDate, String imageUrl, String eraId, String summary, String imageContent, String createBy, String updateBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contents = contents;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imageUrl = imageUrl;
        this.eraId = eraId;
        this.summary = summary;
        this.imageContent = imageContent;
        this.createBy = createBy;
        this.updateBy = updateBy;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContents() { return contents; }
    public void setContents(String contents) { this.contents = contents; }

    public int getStartDate() { return startDate; }
    public void setStartDate(int startDate) { this.startDate = startDate; }

    public int getEndDate() { return endDate; }
    public void setEndDate(int endDate) { this.endDate = endDate; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getEraId() { return eraId; }
    public void setEraId(String eraId) { this.eraId = eraId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getImageContent() { return imageContent; }
    public void setImageContent(String imageContent) { this.imageContent = imageContent; }

    public String getCreateBy() { return createBy; }
    public void setCreateBy(String createBy) { this.createBy = createBy; }

    public String getUpdateBy() { return updateBy; }
    public void setUpdateBy(String updateBy) { this.updateBy = updateBy; }
} 