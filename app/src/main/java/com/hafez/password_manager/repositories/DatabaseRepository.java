package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.App;
import com.hafez.password_manager.database.AppDatabase;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfoFull;
import java.util.List;
import java.util.concurrent.Executor;

public class DatabaseRepository implements LoginInfoRepository {

    private Executor databaseExecutor;
    private LoginInfoDao dao;

    public DatabaseRepository() {
        this(AppDatabase.getInstance().getLoginInfoDao());
    }

    public DatabaseRepository(LoginInfoDao dao) {
        this.databaseExecutor = App.getInstance().getDatabaseExecutor();
        this.dao = dao;
    }

    @Override
    public void insert(LoginInfoFull loginInfo) {
        databaseExecutor.execute(() -> dao.insert(loginInfo));
    }

    @Override
    public void update(LoginInfoFull loginInfo) {
        databaseExecutor.execute(() -> dao.update(loginInfo));
    }

    @Override
    public void delete(LoginInfoFull loginInfo) {
        databaseExecutor.execute(() -> dao.delete(loginInfo));
    }

    @Override
    public void deleteAllLoginInfo() {
        databaseExecutor.execute(() -> dao.deleteAllLoginInfo());
    }

    @Override
    public LiveData<List<LoginInfoFull>> getAllLoginInfoList() {
        return dao.getLoginInfoList();
    }

    @Override
    public LiveData<LoginInfoFull> getLoginInfo(long id) {
        return dao.getLoginInfo(id);
    }

    @Override
    public void insertCategory(Category category) {
        databaseExecutor.execute(() -> dao.insertCategory(category));
    }

    @Override
    public void updateCategory(Category category) {
        databaseExecutor.execute(() -> dao.updateCategory(category));
    }

    @Override
    public void deleteCategory(Category category) {
        databaseExecutor.execute(() -> dao.deleteCategory(category));
    }

    @Override
    public LiveData<List<Category>> getAllCategories() {
        return dao.getCategoryList();
    }

}
