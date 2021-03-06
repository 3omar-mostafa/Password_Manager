package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfoFull;
import java.util.List;

public interface LoginInfoRepository {

    void insert(LoginInfoFull loginInfo);

    void update(LoginInfoFull loginInfo);

    void delete(LoginInfoFull loginInfo);

    void deleteAllLoginInfo();

    LiveData<List<LoginInfoFull>> getAllLoginInfoList();

    LiveData<LoginInfoFull> getLoginInfo(long id);

    void reorderLoginInfoPositions(int startMovePosition, int endMovePosition);

    void insertCategory(Category category);

    void updateCategory(Category category);

    void deleteCategory(Category category);

    LiveData<List<Category>> getAllCategories();

}
