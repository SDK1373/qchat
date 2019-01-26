package com.android.sdk13.qchat.Data;

public class Friend {
    public String imagePath;
    public String name;

    public String getImagePath() {
        return imagePath;
    }

    public String getName() {
        return name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Friend{" +
                "imagePath='" + imagePath + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
