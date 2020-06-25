package com.hafez.password_manager;

import android.os.Bundle;
import android.view.View.OnFocusChangeListener;
import android.view.ViewParent;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.hafez.password_manager.databinding.ActivityAddEditLoginInfoBinding;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;

public class AddEditLoginInfoActivity extends AppCompatActivity {

    protected AddEditLoginInfoViewModel viewModel;
    protected ActivityAddEditLoginInfoBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityAddEditLoginInfoBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        viewModel = new ViewModelProvider(this, new AddEditLoginInfoViewModel.Factory())
                .get(AddEditLoginInfoViewModel.class);

        initializeViews();

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
            if (isInputValid()) {
                saveLoginInfo();
                finish();
            } else {
                showSavingErrors();
            }
        });

    }

    protected boolean isInputValid() {
        return isValidTextInput(viewBinding.username) && isValidTextInput(viewBinding.password);
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

    protected void ifInvalidShowErrorElseRemoveError(@NonNull EditText editText, boolean isValid) {
        if (isValid) {
            clearErrorMessage(editText);
        } else {
            showErrorMessage(editText);
        }
    }

    protected void saveLoginInfo() {
        String username = viewBinding.username.getText().toString();
        String password = viewBinding.password.getText().toString();

        LoginInfo loginInfo = new LoginInfo(username, password, R.drawable.ic_launcher);
        viewModel.insertOrUpdateLoginInfo(loginInfo);
    }

    protected boolean isValidTextInput(@NonNull EditText editText) {
        return editText.getText() != null && !editText.getText().toString().trim().isEmpty();
    }

    protected void showErrorMessage(@NonNull EditText editText) {
        showErrorMessage(editText, R.string.error_text);
    }

    protected void showErrorMessage(@NonNull EditText editText, @StringRes int errorMessageResId) {
        showErrorMessage(editText, getString(errorMessageResId));
    }

    protected void showErrorMessage(@NonNull EditText editText, String errorMessage) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(errorMessage);
    }

    protected void clearErrorMessage(@NonNull EditText editText) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(null);
    }


    protected TextInputLayout getParentTextInputLayout(@NonNull EditText editText)
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
}