package org.telegram.toolbox.toolbox.models;

public class Source {
    private String label;
    private String author;
    private Access access;
    private String type;
    private String fileId;
    private long timestamp;

    public enum Access {
        PUBLIC,
        PRIVATE
    }

    public String getLabel() {
        return label;
    }

    public Source setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getAuthor() {
        return author;
    }

    public Source setAuthor(String author) {
        this.author = author;
        return this;
    }

    public Access getAccess() {
        return access;
    }

    public Source setAccess(Access access) {
        this.access = access;
        return this;
    }

    public String getType() {
        return type;
    }

    public Source setType(String type) {
        this.type = type;
        return this;
    }

    public String getFileId() {
        return fileId;
    }

    public Source setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Source setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
