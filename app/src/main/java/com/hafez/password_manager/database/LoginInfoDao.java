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

    @Insert(entity = LoginInfo.class,onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(LoginInfoFull loginInfo);

    @Update(entity = LoginInfo.class)
    public abstract void update(LoginInfoFull loginInfo);

    @Delete(entity = LoginInfo.class)
    public abstract void delete(LoginInfoFull loginInfo);

    @Query("DELETE FROM LoginInfo")
    public abstract void deleteAllLoginInfo();

    @Query("SELECT * FROM LoginInfo")
    public abstract LiveData<List<LoginInfoFull>> getLoginInfoList();

    @Query("SELECT * FROM LoginInfo WHERE id = :id")
    public abstract LiveData<LoginInfoFull> getLoginInfo(long id);

}
