package com.hafez.password_manager.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Ignore;
import java.util.Objects;

public class LoginInfoFull {

    @Embedded
    private LoginInfo loginInfo;

    @Ignore
    public LoginInfoFull(@NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId) {
        this.loginInfo = new LoginInfo(username, password, iconResourceId);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId) {
        this.loginInfo = new LoginInfo(id, username, password, iconResourceId);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId, long lastEditTime) {
        this.loginInfo = new LoginInfo(id, username, password, iconResourceId, lastEditTime);
    }

    public LoginInfoFull(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public LoginInfo getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(LoginInfo loginInfo) {
        this.loginInfo = loginInfo;
    }

    public long getId() {
        return loginInfo.id;
    }

    public void setId(long id) {
        this.loginInfo.id = id;
    }

    @DrawableRes
    public int getIconResourceId() {
        return loginInfo.iconResourceId;
    }

    public void setIconResourceId(@DrawableRes int iconResourceId) {
        this.loginInfo.iconResourceId = iconResourceId;
    }

    @NonNull
    public String getUsername() {
        return loginInfo.username;
    }

    public void setUsername(@NonNull String username) {
        this.loginInfo.username = username;
    }

    @NonNull
    public String getPassword() {
        return loginInfo.password;
    }

    public void setPassword(@NonNull String password) {
        this.loginInfo.password = password;
    }

    public long getLastEditTime() {
        return loginInfo.lastEditTime;
    }

    public void setLastEditTime(long time) {
        this.loginInfo.lastEditTime = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LoginInfoFull that = (LoginInfoFull) obj;
        return Objects.equals(loginInfo, that.loginInfo);
    }

}
