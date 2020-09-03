package com.hafez.password_manager.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.hafez.password_manager.App;
import com.hafez.password_manager.Converters;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfo;


@TypeConverters(Converters.class)
@Database(entities = {LoginInfo.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract LoginInfoDao getLoginInfoDao();

    public static AppDatabase getInstance() {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room
                            .databaseBuilder(App.getInstance(), AppDatabase.class, "login_info.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return instance;
    }

}
