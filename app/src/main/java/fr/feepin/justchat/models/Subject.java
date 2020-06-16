package fr.feepin.justchat.models;

import android.graphics.drawable.Drawable;

public class Subject {
    private String name;
    private boolean isLiked;
    private int userCount;
    private Drawable backgroundImg;
    private float translationX, translationY;

    public Subject(String name, boolean isLiked, int userCount, Drawable backgroundImg, float translationX, float translationY){
        this.name = name;
        this.isLiked = isLiked;
        this.userCount = userCount;
        this.backgroundImg = backgroundImg;
        this.translationX = translationX;
        this.translationY = translationY;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public Drawable getBackgroundImg() {
        return backgroundImg;
    }

    public void setBackgroundImg(Drawable backgroundImg) {
        this.backgroundImg = backgroundImg;
    }


    public float getTranslationX() {
        return translationX;
    }

    public void setTranslationX(float translationX) {
        this.translationX = translationX;
    }

    public float getTranslationY() {
        return translationY;
    }

    public void setTranslationY(float translationY) {
        this.translationY = translationY;
    }
}
