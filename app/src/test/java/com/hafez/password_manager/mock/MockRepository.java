package com.hafez.password_manager.mock;

import android.database.sqlite.SQLiteConstraintException;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.ArrayList;
import java.util.List;

public class MockRepository implements LoginInfoRepository {

    private MutableLiveData<List<LoginInfoFull>> database;
    private List<Category> categoryTable = new ArrayList<>();


    public MockRepository(@NonNull List<LoginInfoFull> loginInfoList) {
        database = new MutableLiveData<>(new ArrayList<>(loginInfoList));
        for (LoginInfoFull loginInfo : loginInfoList) {
            categoryTable.add(loginInfo.getCategory());
        }
    }

    public MockRepository(@NonNull List<LoginInfoFull> loginInfoList,
            @NonNull List<Category> categoryList) {
        database = new MutableLiveData<>(new ArrayList<>(loginInfoList));
        categoryTable = new ArrayList<>(categoryList);

        for (LoginInfoFull loginInfo : loginInfoList) {
            if (!doesCategoriesContains(loginInfo.getCategoryName())) {
                categoryTable.add(loginInfo.getCategory());
            }
        }
    }

    public MockRepository() {
        database = new MutableLiveData<>(new ArrayList<>());
    }


    @Override
    public void insert(LoginInfoFull loginInfo) {
        if (loginInfo.getCategory() != null) {
            insertCategory(loginInfo.getCategory());
        }

        checkCategoryForeignKey(loginInfo.getLoginInfo().getCategoryName());

        List<LoginInfoFull> loginInfoList = database.getValue();

        if (containsId(loginInfoList, loginInfo.getId())) {
            update(loginInfo);
            return;
        }

        loginInfoList.add(loginInfo);
        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void update(LoginInfoFull loginInfo) {
        if (loginInfo.getCategory() != null) {
            updateCategory(loginInfo.getCategory());
        }

        checkCategoryForeignKey(loginInfo.getLoginInfo().getCategoryName());

        List<LoginInfoFull> loginInfoList = database.getValue();

        for (int i = 0; i < loginInfoList.size(); i++) {
            if (loginInfoList.get(i).getId() == loginInfo.getId()) {
                loginInfoList.remove(i);
                loginInfoList.add(i, loginInfo);
                break;
            }
        }

        database.setValue(new ArrayList<>(loginInfoList));
    }

    @Override
    public void delete(LoginInfoFull loginInfo) {
        List<LoginInfoFull> loginInfoList = database.getValue();

        for (int i = 0; i < loginInfoList.size(); i++) {
            if (loginInfoList.get(i).getId() == loginInfo.getId()) {
                loginInfoList.remove(i);
                break;
            }
        }

        database.setValue(new ArrayList<>(loginInfoList));
    }

    private boolean containsId(List<LoginInfoFull> list, long id) {
        for (LoginInfoFull loginInfo : list) {
            if (loginInfo.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean doesCategoriesContains(String name) {
        for (Category category : categoryTable) {
            if (category.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private void checkCategoryForeignKey(String name) {
        if (name == null) {
            return;
        }

        if (!doesCategoriesContains(name)) {
            throw new SQLiteConstraintException(
                    "FOREIGN KEY constraint failed, Key not found in Category Table");
        }
    }

    @Override
    public void deleteAllLoginInfo() {
        database.setValue(new ArrayList<>());
    }

    @Override
    public LiveData<List<LoginInfoFull>> getAllLoginInfoList() {
        return database;
    }

    @Override
    public LiveData<LoginInfoFull> getLoginInfo(long id) {
        MutableLiveData<LoginInfoFull> loginInfoLiveData = new MutableLiveData<>();

        for (LoginInfoFull loginInfo : database.getValue()) {
            if (loginInfo.getId() == id) {
                loginInfoLiveData.setValue(loginInfo);
                break;
            }
        }
        return loginInfoLiveData;
    }

    @Override
    public void insertCategory(Category category) {

        if (doesCategoriesContains(category.getName())) {
            updateCategory(category);
            return;
        }
        categoryTable.add(category);
    }

    @Override
    public void updateCategory(Category category) {

        for (int i = 0; i < categoryTable.size(); i++) {
            if (categoryTable.get(i).getName().equals(category.getName())) {
                categoryTable.remove(i);
                categoryTable.add(category);
                break;
            }
        }
    }

    @Override
    public void deleteCategory(Category category) {

        for (int i = 0; i < categoryTable.size(); i++) {
            if (categoryTable.get(i).getName().equals(category.getName())) {
                categoryTable.remove(i);
                break;
            }
        }
    }
}
