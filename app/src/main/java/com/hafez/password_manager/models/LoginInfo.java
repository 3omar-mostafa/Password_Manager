package com.hafez.password_manager.models;

import androidx.annotation.IdRes;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity
public class LoginInfo {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @IdRes
    private int iconResourceId;

    private String username;

    private String password;

    public LoginInfo(String username, String password, int iconResourceId) {
        this.username = username;
        this.password = password;
        this.iconResourceId = iconResourceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return id == loginInfo.id &&
                iconResourceId == loginInfo.iconResourceId &&
                Objects.equals(username, loginInfo.username) &&
                Objects.equals(password, loginInfo.password);
    }
}
