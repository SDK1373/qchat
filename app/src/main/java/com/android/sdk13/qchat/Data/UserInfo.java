package com.android.sdk13.qchat.Data;

public class UserInfo {
    private int id;
    private String username;
    private String password;
    private int isLogin;

    public UserInfo(int id, String username, String password, int isLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isLogin = isLogin;
    }

    public UserInfo(String username, String password, int isLogin) {
        this.username = username;
        this.password = password;
        this.isLogin = isLogin;
    }

    public UserInfo(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(int isLogin) {
        this.isLogin = isLogin;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isLogin=" + isLogin +
                '}';
    }

}
