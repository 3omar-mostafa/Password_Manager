package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.App;
import com.hafez.password_manager.database.AppDatabase;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.LoginInfo;
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
    public void insert(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.insert(loginInfo));
    }

    @Override
    public void update(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.update(loginInfo));
    }

    @Override
    public void delete(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.delete(loginInfo));
    }

    @Override
    public void deleteAllLoginInfo() {
        databaseExecutor.execute(() -> dao.deleteAllLoginInfo());
    }

    @Override
    public LiveData<List<LoginInfo>> getAllLoginInfoList() {
        return dao.getLoginInfoList();
    }

    @Override
    public LiveData<LoginInfo> getLoginInfo(long id) {
        return dao.getLoginInfo(id);
    }

}
