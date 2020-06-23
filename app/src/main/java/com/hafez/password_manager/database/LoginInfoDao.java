package com.hafez.password_manager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;

@Dao
public interface LoginInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginInfo loginInfo);

    @Update
    void update(LoginInfo loginInfo);

    @Delete
    void delete(LoginInfo loginInfo);

    @Query("DELETE FROM LoginInfo")
    void deleteAllLoginInfo();

    @Query("SELECT * FROM LoginInfo")
    LiveData<List<LoginInfo>> getLoginInfoList();

    @Query("SELECT * FROM LoginInfo WHERE id = :id")
    LiveData<LoginInfo> getLoginInfo(long id);

}
