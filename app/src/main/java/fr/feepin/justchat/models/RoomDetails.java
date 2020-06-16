package fr.feepin.justchat.models;

public class RoomDetails {
    private String name;
    private int userCount;
    private int colorId;

    public RoomDetails(String name, int userCount, int colorId){
        this.colorId = colorId;
        this.name = name;
        this.userCount = userCount;
    }

    public String getName() {
        return name;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }
}
