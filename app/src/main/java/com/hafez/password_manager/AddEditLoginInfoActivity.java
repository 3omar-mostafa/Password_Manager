package com.hafez.password_manager;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hafez.password_manager.databinding.ActivityAddEditLoginInfoBinding;
import com.hafez.password_manager.models.Category;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddEditLoginInfoActivity extends AppCompatActivity {

    public static final String ARGUMENT_LOGIN_INFO_ID = "id";

    protected AddEditLoginInfoViewModel viewModel;
    protected ActivityAddEditLoginInfoBinding viewBinding;

    enum Modes {ADD, EDIT}
    private Modes mode = Modes.ADD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityAddEditLoginInfoBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        long id = getIntent().getLongExtra(ARGUMENT_LOGIN_INFO_ID, LoginInfo.INVALID_ID);
        if (id != LoginInfo.INVALID_ID) {
            mode = Modes.EDIT;
        }

        viewModel = getViewModel(new AddEditLoginInfoViewModel.Factory(id));

        LiveData<LoginInfoFull> loginInfoData = viewModel.getLoginInfo();

        if (mode == Modes.EDIT) {
            setTitle(R.string.edit_login_info);
            if (loginInfoData != null) {
                loginInfoData.observe(this, loginInfo -> bindViewsWithData(loginInfo));
            }
        }

        initializeCategories();

        initializeViews();

    }


    /**
     * The real usage of this method is in testing, to have the ability to inject mocked view model
     *
     * @param factory Factory to create view model with it
     *
     * @return the created view model
     */
    protected AddEditLoginInfoViewModel getViewModel(ViewModelProvider.Factory factory) {
        return new ViewModelProvider(this, factory).get(AddEditLoginInfoViewModel.class);
    }

    protected void bindViewsWithData(@NonNull LoginInfoFull loginInfo) {
        viewBinding.username.setText(loginInfo.getUsername());
        viewBinding.password.setText(loginInfo.getPassword());

        viewBinding.lastEdited.setVisibility(View.VISIBLE);
        viewBinding.lastEdited.setText(getFormattedDateTime(loginInfo.getLastEditTime()));

        viewBinding.username.clearFocus();
        viewBinding.password.clearFocus();
    }

    protected static String getFormattedDateTime(long time) {

        long currentTime = Calendar.getInstance().getTimeInMillis();

        return String.format("%s :\n%s\n%s",
                App.getInstance().getString(R.string.last_edited),
                DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(time),
                DateUtils.getRelativeTimeSpanString(time, currentTime, DateUtils.MINUTE_IN_MILLIS)
        );
    }

    protected void initializeViews() {
        OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            if (!hasFocus) {
                EditText editText = (EditText) view;
                ifInvalidShowErrorElseRemoveError(editText);
            }
        };

        viewBinding.username.requestFocus();

        viewBinding.username.setOnFocusChangeListener(focusChangeListener);
        viewBinding.password.setOnFocusChangeListener(focusChangeListener);

        viewBinding.save.setOnClickListener(v -> {
            checkInputAndSave();
        });

    }


    protected void initializeCategories() {
        // TODO : Use Real Data from Database
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Category 1", R.drawable.ic_launcher));
        categories.add(new Category("Category 2", R.drawable.ic_launcher));
        categories.add(new Category("Category 3", R.drawable.ic_launcher));

        CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, categories);

        adapter.setHint(new Category(getString(R.string.category), R.drawable.icon_category));

        viewBinding.categoryDropdownList.setAdapter(adapter);
        viewBinding.categoryDropdownList.setSelection(adapter.getHintPosition());
    }

    protected boolean isInputValid() {
        return isValidTextInput(viewBinding.username) && isValidTextInput(viewBinding.password);
    }

    private void checkInputAndSave() {
        if (isInputValid()) {
            saveLoginInfo();
            finish();
        } else {
            showSavingErrors();
        }
    }

    protected void showSavingErrors() {
        Snackbar.make(viewBinding.save, R.string.error_save_validation, Snackbar.LENGTH_SHORT)
                .setAnchorView(viewBinding.save)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .show();

        ifInvalidShowErrorElseRemoveError(viewBinding.username);
        ifInvalidShowErrorElseRemoveError(viewBinding.password);
    }

    protected void ifInvalidShowErrorElseRemoveError(@NonNull EditText editText) {
        ifInvalidShowErrorElseRemoveError(editText, isValidTextInput(editText));
    }

    protected static void ifInvalidShowErrorElseRemoveError(@NonNull EditText editText,
            boolean isValid) {
        if (isValid) {
            clearErrorMessage(editText);
        } else {
            showErrorMessage(editText);
        }
    }

    protected void saveLoginInfo() {
        String username = viewBinding.username.getText().toString();
        String password = viewBinding.password.getText().toString();

        Category category = null;
        if (isAnyCategorySelected()) {
            category = (Category) viewBinding.categoryDropdownList.getSelectedItem();
        }

        LoginInfoFull loginInfo = new LoginInfoFull(username, password, category);
        viewModel.insertOrUpdateLoginInfo(loginInfo);
    }

    protected boolean isAnyCategorySelected() {
        Spinner categoryDropdownList = viewBinding.categoryDropdownList;
        int selectedPosition = categoryDropdownList.getSelectedItemPosition();
        CategorySpinnerAdapter adapter = (CategorySpinnerAdapter) categoryDropdownList.getAdapter();

        return selectedPosition != adapter.getHintPosition();
    }

    protected static boolean isValidTextInput(@NonNull EditText editText) {
        return editText.getText() != null && !editText.getText().toString().trim().isEmpty();
    }

    protected static void showErrorMessage(@NonNull EditText editText) {
        showErrorMessage(editText, R.string.error_text);
    }

    protected static void showErrorMessage(@NonNull EditText editText,
            @StringRes int errorMessageResId) {
        showErrorMessage(editText, App.getInstance().getString(errorMessageResId));
    }

    protected static void showErrorMessage(@NonNull EditText editText, String errorMessage) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(errorMessage);
    }

    protected static void clearErrorMessage(@NonNull EditText editText) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(null);
    }


    protected static TextInputLayout getParentTextInputLayout(@NonNull EditText editText)
            throws IllegalStateException {
        TextInputLayout inputLayout;

        // Uses parent of parent because TextInputLayout puts a FrameLayout as a direct child by default
        ViewParent parent = editText.getParent().getParent();
        if (parent instanceof TextInputLayout) {
            inputLayout = (TextInputLayout) parent;
        } else {
            throw new IllegalStateException("Parent View Must be TextInputLayout");
        }
        return inputLayout;
    }


    @Override
    public void onBackPressed() {
        if (userMadeChanges()) {
            showSaveOrDiscardDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (userMadeChanges()) {
                showSaveOrDiscardDialog();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSaveOrDiscardDialog() {
        new Builder(this)
                .setMessage(R.string.unsaved_changes)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    dialog.dismiss();
                    checkInputAndSave();
                })
                .setNegativeButton(R.string.discard, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .create()
                .show();
    }

    @Nullable
    private static String getString(@NonNull EditText editText) {
        return editText.getText() == null ? null : editText.getText().toString();
    }

    protected boolean userMadeChanges() {

        if (mode == Modes.ADD) {
            return isValidTextInput(viewBinding.username) || isValidTextInput(viewBinding.password);
        } else {
            assert viewModel.getLoginInfo() != null;
            LoginInfoFull loginInfo = viewModel.getLoginInfo().getValue();
            assert loginInfo != null;

            return !(Objects.equals(loginInfo.getUsername(), getString(viewBinding.username)) &&
                    Objects.equals(loginInfo.getPassword(), getString(viewBinding.password)));
        }
    }
}