package com.hafez.password_manager.repositories;

import androidx.lifecycle.LiveData;
import com.hafez.password_manager.App;
import com.hafez.password_manager.database.AppDatabase;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.LoginInfo;
import java.util.List;
import java.util.concurrent.Executor;

public class DatabaseRepository implements LoginInfoRepository {

    private Executor databaseExecutor = App.getInstance().getDatabaseExecutor();
    private LoginInfoDao dao = AppDatabase.getInstance().getLoginInfoDao();

    @Override
    public void insert(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.insert(loginInfo));
    }

    @Override
    public void update(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.insert(loginInfo));
    }

    @Override
    public void delete(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.insert(loginInfo));
    }

    @Override
    public void deleteAllLoginInfo(LoginInfo loginInfo) {
        databaseExecutor.execute(() -> dao.deleteAllLoginInfo());
    }

    @Override
    public LiveData<List<LoginInfo>> getAllLoginInfoList() {
        return dao.getLoginInfoList();
    }

}
