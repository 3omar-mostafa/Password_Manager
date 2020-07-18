package com.hafez.password_manager.models;

import androidx.annotation.IdRes;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.Objects;

@Entity
public class LoginInfo {

    public static final long INVALID_ID = 0;

    @PrimaryKey(autoGenerate = true)
    private long id = INVALID_ID;

    @IdRes
    private int iconResourceId;

    private String username;

    private String password;

    /**
     * Time represented as number of milliseconds since the epoch, January 1, 1970, 00:00:00 GMT.
     */
    private long lastEditTime;

    @Ignore
    public LoginInfo(String username, String password, int iconResourceId) {
        this.username = username;
        this.password = password;
        this.iconResourceId = iconResourceId;
        this.lastEditTime = Calendar.getInstance().getTimeInMillis();
    }

    public LoginInfo(long id, String username, String password, int iconResourceId,
            long lastEditTime) {
        this(username, password, iconResourceId);
        this.id = id;
        this.lastEditTime = lastEditTime;
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

    public long getLastEditTime() {
        return lastEditTime;
    }

    public void setLastEditTime(long time) {
        this.lastEditTime = time;
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
