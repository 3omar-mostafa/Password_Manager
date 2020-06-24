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

    private AddEditLoginInfoViewModel viewModel;
    private ActivityAddEditLoginInfoBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityAddEditLoginInfoBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        viewModel = new ViewModelProvider(this, new AddEditLoginInfoViewModel.Factory())
                .get(AddEditLoginInfoViewModel.class);

        initializeViews();

    }

    private void initializeViews() {
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

    private boolean isInputValid() {
        return isValidTextInput(viewBinding.username) && isValidTextInput(viewBinding.password);
    }

    private void showSavingErrors() {
        Snackbar.make(viewBinding.save, R.string.error_save_validation, Snackbar.LENGTH_SHORT)
                .setAnchorView(viewBinding.save)
                .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_FADE)
                .show();

        ifInvalidShowErrorElseRemoveError(viewBinding.username);
        ifInvalidShowErrorElseRemoveError(viewBinding.password);
    }

    private void ifInvalidShowErrorElseRemoveError(@NonNull EditText editText) {
        ifInvalidShowErrorElseRemoveError(editText, isValidTextInput(editText));
    }

    private void ifInvalidShowErrorElseRemoveError(@NonNull EditText editText, boolean isValid) {
        if (isValid) {
            clearErrorMessage(editText);
        } else {
            showErrorMessage(editText);
        }
    }

    private void saveLoginInfo() {
        String username = viewBinding.username.getText().toString();
        String password = viewBinding.password.getText().toString();

        LoginInfo loginInfo = new LoginInfo(username, password, R.drawable.ic_launcher);
        viewModel.insertOrUpdateLoginInfo(loginInfo);
    }

    private boolean isValidTextInput(@NonNull EditText editText) {
        return editText.getText() != null && !editText.getText().toString().trim().isEmpty();
    }

    private void showErrorMessage(@NonNull EditText editText) {
        showErrorMessage(editText, R.string.error_text);
    }

    private void showErrorMessage(@NonNull EditText editText, @StringRes int errorMessageResId) {
        showErrorMessage(editText, getString(errorMessageResId));
    }

    private void showErrorMessage(@NonNull EditText editText, String errorMessage) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(errorMessage);
    }

    private void clearErrorMessage(@NonNull EditText editText) {
        TextInputLayout inputLayout = getParentTextInputLayout(editText);
        inputLayout.setError(null);
    }


    private TextInputLayout getParentTextInputLayout(@NonNull EditText editText) {
        TextInputLayout inputLayout;

        ViewParent parent = editText.getParent();
        if (parent instanceof TextInputLayout) {
            inputLayout = (TextInputLayout) parent;
        } else if (parent.getParent() instanceof TextInputLayout) {
            inputLayout = (TextInputLayout) parent.getParent();
        } else {
            throw new IllegalStateException("Parent View Must be TextInputLayout");
        }
        return inputLayout;
    }
}