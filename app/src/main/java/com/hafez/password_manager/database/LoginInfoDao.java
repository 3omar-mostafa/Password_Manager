package com.hafez.password_manager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.models.LoginInfoFull;
import java.util.List;

@Dao
public abstract class LoginInfoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(LoginInfoFull loginInfo) {
        if (loginInfo.getCategory() != null) {
            insertCategory(loginInfo.getCategory());
        }
        insert(loginInfo.getLoginInfo());
    }

    @Transaction
    @Update
    public void update(LoginInfoFull loginInfo) {
        if (loginInfo.getCategory() != null) {
            updateCategory(loginInfo.getCategory());
        }
        update(loginInfo.getLoginInfo());
    }

    @Transaction
    @Delete
    public void delete(LoginInfoFull loginInfo) {
        delete(loginInfo.getLoginInfo());
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(LoginInfo loginInfo);

    @Update
    public abstract void update(LoginInfo loginInfo);

    @Delete
    public abstract void delete(LoginInfo loginInfo);

    @Query("DELETE FROM LoginInfo")
    public abstract void deleteAllLoginInfo();

    @Transaction
    @Query("SELECT * FROM LoginInfo")
    public abstract LiveData<List<LoginInfoFull>> getLoginInfoList();

    @Transaction
    @Query("SELECT * FROM LoginInfo WHERE id = :id")
    public abstract LiveData<LoginInfoFull> getLoginInfo(long id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertCategory(Category category);

    @Update
    public abstract void updateCategory(Category category);

    @Delete
    public abstract void deleteCategory(Category category);

    @Query("SELECT * FROM Category ORDER BY name")
    public abstract LiveData<List<Category>> getCategoryList();

}
