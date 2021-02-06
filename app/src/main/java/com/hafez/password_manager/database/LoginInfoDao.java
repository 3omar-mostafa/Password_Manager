package com.hafez.password_manager.database;

import androidx.annotation.NonNull;
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
    public void insert(@NonNull LoginInfoFull loginInfo) {
        insert(loginInfo.getLoginInfo());
    }

    @Transaction
    @Update
    public void update(@NonNull LoginInfoFull loginInfo) {
        update(loginInfo.getLoginInfo());
    }

    @Transaction
    @Delete
    public void delete(@NonNull LoginInfoFull loginInfo) {
        delete(loginInfo.getLoginInfo());
    }

    @Query("UPDATE LoginInfo SET id=:newId WHERE id=:oldId")
    protected abstract void updateLoginInfoId(long oldId, long newId);

    @Transaction
    protected void swapIds(LoginInfoFull loginInfo_1, LoginInfoFull loginInfo_2) {
        updateLoginInfoId(loginInfo_1.getId(), LoginInfo.INVALID_ID);
        updateLoginInfoId(loginInfo_2.getId(), loginInfo_1.getId());
        updateLoginInfoId(LoginInfo.INVALID_ID, loginInfo_2.getId());
    }

    /**
     * Reorder the database entries by changing their ids instead of adding another column of
     * positions. An Entry is dragged from {@code startMovePosition} to {@code endMovePosition} and
     * the entries between the two positions are moved upwards or downwards based on direction of
     * moving. This is typically used by drag and drop to reorder list.
     *
     * @param startMovePosition The position where moving started (old position)
     * @param endMovePosition   The position where moving ended (new position)
     */
    @Transaction
    public void reorderLoginInfoPositions(int startMovePosition, int endMovePosition) {
        List<LoginInfoFull> loginInfoList = getLoginInfoListNoLiveData();

        if (startMovePosition < endMovePosition) {
            for (int i = startMovePosition; i < endMovePosition; i++) {
                swapIds(loginInfoList.get(i), loginInfoList.get(i + 1));
            }
        } else {
            for (int i = startMovePosition; i > endMovePosition; i--) {
                swapIds(loginInfoList.get(i), loginInfoList.get(i - 1));
            }
        }

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
    @Query("SELECT * FROM LoginInfo ORDER BY id")
    protected abstract List<LoginInfoFull> getLoginInfoListNoLiveData();

    @Transaction
    @Query("SELECT * FROM LoginInfo ORDER BY id")
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
