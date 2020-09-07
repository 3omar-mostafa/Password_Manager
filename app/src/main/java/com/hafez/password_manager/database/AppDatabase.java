package com.hafez.password_manager.database;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.hafez.password_manager.App;
import com.hafez.password_manager.Converters;
import com.hafez.password_manager.R;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfo;


@TypeConverters(Converters.class)
@Database(entities = {LoginInfo.class, Category.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    private static Callback prepopulateCategoriesOnCreateCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            AsyncTask.execute(() -> {
                LoginInfoDao dao = instance.getLoginInfoDao();

                App app = App.getInstance();
                dao.insertCategory(new Category(app.getString(R.string.adobe), R.drawable.adobe));
                dao.insertCategory(new Category(app.getString(R.string.amazon), R.drawable.amazon));
                dao.insertCategory(new Category(app.getString(R.string.facebook), R.drawable.facebook));
                dao.insertCategory(new Category(app.getString(R.string.github), R.drawable.github));
                dao.insertCategory(new Category(app.getString(R.string.gitlab), R.drawable.gitlab));
                dao.insertCategory(new Category(app.getString(R.string.google), R.drawable.google));
                dao.insertCategory(new Category(app.getString(R.string.instagram), R.drawable.instagram));
                dao.insertCategory(new Category(app.getString(R.string.linkedin), R.drawable.linkedin));
                dao.insertCategory(new Category(app.getString(R.string.linux), R.drawable.linux));
                dao.insertCategory(new Category(app.getString(R.string.microsoft), R.drawable.microsoft));
                dao.insertCategory(new Category(app.getString(R.string.twitter), R.drawable.twitter));
                dao.insertCategory(new Category(app.getString(R.string.ubuntu), R.drawable.ubuntu));
                dao.insertCategory(new Category(app.getString(R.string.yahoo), R.drawable.yahoo));
            });
        }
    };

    public abstract LoginInfoDao getLoginInfoDao();

    public static AppDatabase getInstance() {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room
                            .databaseBuilder(App.getInstance(), AppDatabase.class, "login_info.db")
                            .fallbackToDestructiveMigration()
                            .addCallback(prepopulateCategoriesOnCreateCallback)
                            .build();
                }
            }
        }

        return instance;
    }

}
