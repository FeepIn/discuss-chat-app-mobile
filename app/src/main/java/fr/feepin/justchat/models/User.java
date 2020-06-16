package fr.feepin.justchat.models;

public class User {
    private String name, message;
    private boolean isHost;
    private String color;
    private String userJoined;
    public User(String name, boolean isHost, String message, String color){
        this.color = color;
        this.name = name;
        this.isHost = isHost;
        this.message = message;
    }

    public void setUserJoined(String userJoined) {
        this.userJoined = userJoined;
    }

    public String getUserJoined() {
        return userJoined;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
