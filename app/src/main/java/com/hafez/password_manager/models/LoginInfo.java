package com.hafez.password_manager.models;

import androidx.annotation.IdRes;
import java.util.Objects;

public class LoginInfo {

    @IdRes
    private int iconResourceId;

    private String username;

    private String password;

    public LoginInfo(String username, String password, int iconResourceId) {
        this.username = username;
        this.password = password;
        this.iconResourceId = iconResourceId;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoginInfo loginInfo = (LoginInfo) obj;
        return iconResourceId == loginInfo.iconResourceId &&
                Objects.equals(username, loginInfo.username) &&
                Objects.equals(password, loginInfo.password);
    }
}
