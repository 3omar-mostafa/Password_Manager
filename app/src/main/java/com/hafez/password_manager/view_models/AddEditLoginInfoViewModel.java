package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import java.util.List;

public class AddEditLoginInfoViewModel extends ViewModel {

    private LiveData<LoginInfoFull> loginInfo;
    private LoginInfoRepository repository;
    private long loginInfoId;
    private LiveData<List<Category>> categories;

    /**
     * Used when updating existing {@link LoginInfoFull}
     *
     * @param loginInfoId The id of the existing {@link LoginInfoFull} object
     */
    public AddEditLoginInfoViewModel(@NonNull LoginInfoRepository repository, long loginInfoId) {
        this.repository = repository;
        this.loginInfoId = loginInfoId;
        this.loginInfo = repository.getLoginInfo(this.loginInfoId);
        this.categories = repository.getAllCategories();
    }

    /**
     * Used when creating a new {@link LoginInfoFull}
     */
    public AddEditLoginInfoViewModel(@NonNull LoginInfoRepository repository) {
        this.repository = repository;
        this.loginInfoId = LoginInfo.INVALID_ID;
        this.loginInfo = null;
        this.categories = repository.getAllCategories();
    }


    /**
     * Insert new {@link LoginInfoFull} Or Updates existing one bases on the current state. If {@link
     * #loginInfoId} is null then we are creating a new object , if not then we are editing existing
     * one.
     *
     * @param newLoginInfo New {@link LoginInfoFull} object to be inserted or updated
     */
    public void insertOrUpdateLoginInfo(@NonNull LoginInfoFull newLoginInfo) {
        if (loginInfoId == LoginInfo.INVALID_ID) {
            repository.insert(newLoginInfo);
        } else {
            newLoginInfo.setId(loginInfoId);
            repository.update(newLoginInfo);
        }
    }

    public void deleteLoginInfo(LoginInfoFull loginInfo) {
        repository.delete(loginInfo);
    }

    /**
     * @return The {@link LoginInfoFull} we are editing wrapped in {@link LiveData} object. Return
     * null if we are creating a new {@link LoginInfoFull}.
     */
    @Nullable
    public LiveData<LoginInfoFull> getLoginInfo() {
        return loginInfo;
    }

    public LiveData<List<Category>> getAllCategories() {
        return categories;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private LoginInfoRepository repository;
        private long loginInfoId;

        /**
         * Used when updating existing {@link LoginInfoFull}, Or when creating a new {@link
         * LoginInfoFull} by passing {@link LoginInfo#INVALID_ID} id
         *
         * @param loginInfoId The id of the existing {@link LoginInfoFull} object, Or {@link
         *                    LoginInfo#INVALID_ID} to create new {@link LoginInfoFull} object.
         */
        public Factory(LoginInfoRepository repository, long loginInfoId) {
            this.repository = repository;
            this.loginInfoId = loginInfoId;
        }

        /**
         * Used when creating a new {@link LoginInfoFull}
         */
        public Factory(LoginInfoRepository repository) {
            this(repository, LoginInfo.INVALID_ID);
        }

        /**
         * Used when updating existing {@link LoginInfoFull}
         *
         * @param loginInfoId The id of the existing {@link LoginInfoFull} object
         */
        public Factory(long loginInfoId) {
            this(new DatabaseRepository(), loginInfoId);
        }

        /**
         * Used when creating a new {@link LoginInfoFull}
         */
        public Factory() {
            this(new DatabaseRepository());
        }


        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            if (loginInfoId == LoginInfo.INVALID_ID) {
                return (T) new AddEditLoginInfoViewModel(repository);
            } else {
                return (T) new AddEditLoginInfoViewModel(repository, loginInfoId);
            }
        }
    }

}
