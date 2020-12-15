package org.telegram.toolbox.toolbox.models;

public class User {
    private String id;
    private int Carma;
    private long timestamp;

    public String getId() {
        return id;
    }

    public User setId(String id) {
        this.id = id;
        return this;
    }

    public int getCarma() {
        return Carma;
    }

    public User setCarma(int carma) {
        Carma = carma;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public User setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}
