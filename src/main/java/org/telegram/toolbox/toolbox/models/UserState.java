package org.telegram.toolbox.toolbox.models;

public class UserState {
    private StringBuilder code = new StringBuilder();
    private String type = "python";
    private int carma;

    public StringBuilder getCode() {
        return code;
    }

    public UserState setCode(StringBuilder code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public UserState setType(String type) {
        this.type = type;
        return this;
    }

    public int getCarma() {
        return carma;
    }

    public UserState setCarma(int carma) {
        this.carma = carma;
        return this;
    }
}
