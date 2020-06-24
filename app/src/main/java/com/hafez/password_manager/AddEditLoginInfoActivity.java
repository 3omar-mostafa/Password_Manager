package com.hafez.password_manager;

import android.os.Bundle;
import android.view.View.OnFocusChangeListener;
import android.view.ViewParent;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.hafez.password_manager.databinding.ActivityAddEditLoginInfoBinding;

public class AddEditLoginInfoActivity extends AppCompatActivity {

    private ActivityAddEditLoginInfoBinding viewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = ActivityAddEditLoginInfoBinding.inflate(getLayoutInflater());

        setContentView(viewBinding.getRoot());

        initializeViews();

        viewBinding.save.setOnClickListener(v -> {
            //TODO: Actually Save Before Closing
            finish();
        });

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