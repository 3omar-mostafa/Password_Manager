package com.hafez.password_manager.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.models.LoginInfoFull;
import java.util.List;

@Dao
public interface LoginInfoDao {

    @Insert(entity = LoginInfo.class,onConflict = OnConflictStrategy.REPLACE)
    void insert(LoginInfoFull loginInfo);

    @Update(entity = LoginInfo.class)
    void update(LoginInfoFull loginInfo);

    @Delete(entity = LoginInfo.class)
    void delete(LoginInfoFull loginInfo);

    @Query("DELETE FROM LoginInfo")
    void deleteAllLoginInfo();

    @Query("SELECT * FROM LoginInfo")
    LiveData<List<LoginInfoFull>> getLoginInfoList();

    @Query("SELECT * FROM LoginInfo WHERE id = :id")
    LiveData<LoginInfoFull> getLoginInfo(long id);

}
