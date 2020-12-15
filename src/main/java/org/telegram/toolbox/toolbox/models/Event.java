package org.telegram.toolbox.toolbox.models;

public class Event {
    private String author;
    private String type;
    private String properties;
    private long timestamp;

    public String getAuthor() {
        return author;
    }

    public Event setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getType() {
        return type;
    }

    public Event setType(String type) {
        this.type = type;
        return this;
    }

    public String getProperties() {
        return properties;
    }

    public Event setProperties(String properties) {
        this.properties = properties;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Event setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
