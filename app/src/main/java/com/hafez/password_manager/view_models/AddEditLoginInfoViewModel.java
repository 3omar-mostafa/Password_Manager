package com.hafez.password_manager.view_models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;

public class AddEditLoginInfoViewModel extends ViewModel {

    private LiveData<LoginInfo> loginInfo;
    private LoginInfoRepository repository;
    private Long loginInfoId;

    /**
     * Used when updating existing {@link LoginInfo}
     *
     * @param loginInfoId The id of the existing {@link LoginInfo} object
     */
    public AddEditLoginInfoViewModel(@NonNull LoginInfoRepository repository, long loginInfoId) {
        this.repository = repository;
        this.loginInfoId = loginInfoId;
        this.loginInfo = repository.getLoginInfo(this.loginInfoId);
    }

    /**
     * Used when creating a new {@link LoginInfo}
     */
    public AddEditLoginInfoViewModel(@NonNull LoginInfoRepository repository) {
        this.repository = repository;
        this.loginInfoId = null;
        this.loginInfo = null;
    }


    /**
     * Insert new {@link LoginInfo} Or Updates existing one bases on the current state. If {@link
     * #loginInfoId} is null then we are creating a new object , if not then we are editing existing
     * one.
     *
     * @param newLoginInfo New {@link LoginInfo} object to be inserted or updated
     */
    public void insertOrUpdateLoginInfo(@NonNull LoginInfo newLoginInfo) {
        if (loginInfoId == null) {
            repository.insert(newLoginInfo);
        } else {
            newLoginInfo.setId(loginInfoId);
            repository.update(newLoginInfo);
        }
    }

    public void deleteLoginInfo(LoginInfo loginInfo) {
        repository.delete(loginInfo);
    }

    /**
     * @return The {@link LoginInfo} we are editing wrapped in {@link LiveData} object. Return null
     * if we are creating a new {@link LoginInfo}.
     */
    @Nullable
    public LiveData<LoginInfo> getLoginInfo() {
        return loginInfo;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private LoginInfoRepository repository;
        private Long loginInfoId;

        /**
         * Used when updating existing {@link LoginInfo}
         *
         * @param loginInfoId The id of the existing {@link LoginInfo} object
         */
        public Factory(LoginInfoRepository repository, long loginInfoId) {
            this.repository = repository;
            this.loginInfoId = loginInfoId;
        }

        /**
         * Used when creating a new {@link LoginInfo}
         */
        public Factory(LoginInfoRepository repository) {
            this.repository = repository;
            this.loginInfoId = null;
        }

        /**
         * Used when updating existing {@link LoginInfo}
         *
         * @param loginInfoId The id of the existing {@link LoginInfo} object
         */
        public Factory(long loginInfoId) {
            this(new DatabaseRepository(), loginInfoId);
        }

        /**
         * Used when creating a new {@link LoginInfo}
         */
        public Factory() {
            this(new DatabaseRepository());
        }


        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

            if (loginInfoId == null) {
                return (T) new AddEditLoginInfoViewModel(repository);
            } else {
                return (T) new AddEditLoginInfoViewModel(repository, loginInfoId);
            }
        }
    }

}
