package com.hafez.password_manager.models;

import android.graphics.Bitmap;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.hafez.password_manager.App;
import com.hafez.password_manager.Converters;
import com.hafez.password_manager.R;
import java.util.Objects;

@Entity
public class Category {

    public static final String NO_CATEGORY = App.getInstance().getString(R.string.no_category);

    @DrawableRes
    public static final int NO_CATEGORY_ICON = R.drawable.no_category;


    @NonNull
    @PrimaryKey
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    String name;

    @NonNull
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    Bitmap icon;

    public Category(@NonNull String name, @NonNull Bitmap icon) {
        this.name = name;
        this.icon = icon;
    }

    @Ignore
    public Category(@NonNull String name, @DrawableRes int icon) {
        this.name = name;
        setIcon(icon);
    }


    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(@NonNull Bitmap icon) {
        this.icon = icon;
    }

    public void setIcon(@DrawableRes int icon) {
        this.icon = Converters.drawableToBitmap(icon);
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
        return Objects.equals(name, category.name);
    }

}
