package com.hafez.password_manager.models;

import static androidx.room.ForeignKey.CASCADE;
import static androidx.room.ForeignKey.SET_NULL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.Calendar;
import java.util.Objects;

@Entity(foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "name",
        childColumns = "categoryName", onUpdate = CASCADE, onDelete = SET_NULL))
public class LoginInfo {

    public static final long INVALID_ID = 0;

    @PrimaryKey(autoGenerate = true)
    long id = INVALID_ID;

    @Nullable
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    String categoryName;

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
            @Nullable String categoryName) {
        this.username = username;
        this.password = password;
        this.categoryName = categoryName;
        this.lastEditTime = Calendar.getInstance().getTimeInMillis();
    }

    public LoginInfo(long id, @NonNull String username, @NonNull String password,
            @Nullable String categoryName) {
        this(username, password, categoryName);
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@Nullable String categoryName) {
        this.categoryName = categoryName;
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
                Objects.equals(categoryName, loginInfo.categoryName) &&
                Objects.equals(username, loginInfo.username) &&
                Objects.equals(password, loginInfo.password);
    }
}
