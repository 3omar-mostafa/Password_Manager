package com.hafez.password_manager.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.Objects;

@Entity
public class LoginInfo {

    public static final long INVALID_ID = 0;

    @PrimaryKey(autoGenerate = true)
    long id = INVALID_ID;

    @DrawableRes
    int iconResourceId;

    @NonNull
    String username;

    @NonNull
    String password;

    /**
     * Time represented as number of milliseconds since the epoch, January 1, 1970, 00:00:00 GMT.
     */
    long lastEditTime;

    @Ignore
    public LoginInfo(@NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId) {
        this.username = username;
        this.password = password;
        this.iconResourceId = iconResourceId;
        this.lastEditTime = Calendar.getInstance().getTimeInMillis();
    }

    @Ignore
    public LoginInfo(long id, @NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId) {
        this(username, password, iconResourceId);
        this.id = id;
    }

    public LoginInfo(long id, @NonNull String username, @NonNull String password,
            @DrawableRes int iconResourceId, long lastEditTime) {
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

    @DrawableRes
    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(@DrawableRes int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setPassword(@NonNull String password) {
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
