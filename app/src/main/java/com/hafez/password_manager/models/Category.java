package com.hafez.password_manager.models;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Objects;

@Entity
public class Category {

    @NonNull
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    String name;

    @DrawableRes
    int iconResourceId;

    public Category(@NonNull String name, @DrawableRes int iconResourceId) {
        this.name = name;
        this.iconResourceId = iconResourceId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @DrawableRes
    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(@DrawableRes int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Category category = (Category) obj;
        return iconResourceId == category.iconResourceId &&
                Objects.equals(name, category.name);
    }

}
