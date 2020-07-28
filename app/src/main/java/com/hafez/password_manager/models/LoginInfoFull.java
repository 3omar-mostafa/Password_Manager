package com.hafez.password_manager.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;
import com.hafez.password_manager.R;
import java.util.Objects;

public class LoginInfoFull {

    @Embedded
    private LoginInfo loginInfo;

    @Relation(parentColumn = "categoryName", entityColumn = "name")
    private Category category;

    @Ignore
    public LoginInfoFull(@NonNull String username, @NonNull String password,
            @Nullable Category category) {
        this.category = category;
        String categoryName = (category != null) ? category.getName() : null;
        this.loginInfo = new LoginInfo(username, password, categoryName);
    }


    @Ignore
    public LoginInfoFull(@NonNull String username, @NonNull String password) {
        this(username, password, null);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password,
            @Nullable Category category) {
        this.category = category;
        String categoryName = (category != null) ? category.getName() : null;
        this.loginInfo = new LoginInfo(id, username, password, categoryName);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password) {
        this(id, username, password, null);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password,
            @Nullable Category category, long lastEditTime) {
        this.category = category;
        String categoryName = (category != null) ? category.getName() : null;
        this.loginInfo = new LoginInfo(id, username, password, categoryName, lastEditTime);
    }

    @Ignore
    public LoginInfoFull(long id, @NonNull String username, @NonNull String password,
            long lastEditTime) {
        this(id, username, password, null, lastEditTime);
    }

    @Ignore
    public LoginInfoFull(@NonNull LoginInfo loginInfo) {
        this(loginInfo, null);
    }

    public LoginInfoFull(@NonNull LoginInfo loginInfo, @Nullable Category category) {
        this.loginInfo = loginInfo;
        this.category = category;
        loginInfo.categoryName = (category != null) ? category.getName() : null;
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


    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Nullable
    public String getCategoryName() {
        return (category != null) ? category.name : null;
    }

    public void setCategoryName(@NonNull String categoryName) {
        this.category.name = categoryName;
    }

    @DrawableRes
    public int getIconResourceId() {
        return (category != null) ? category.iconResourceId : R.drawable.ic_launcher;
    }

    public void setIconResourceId(@DrawableRes int iconResourceId) {
        this.category.iconResourceId = iconResourceId;
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
        return Objects.equals(loginInfo, that.loginInfo) &&
                Objects.equals(category, that.category);
    }

}
