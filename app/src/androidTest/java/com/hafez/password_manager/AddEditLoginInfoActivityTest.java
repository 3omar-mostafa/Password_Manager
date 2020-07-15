package com.hafez.password_manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.test.core.app.ApplicationProvider;
import com.google.android.material.textfield.TextInputLayout;
import org.junit.Before;
import org.junit.Test;

/**
 * The {@link AddEditLoginInfoActivity} Class Test is separated into three test classes, class for
 * general functions which is this class, i.e. {@link AddEditLoginInfoActivityTest}  , class for Add
 * part which is this in {@link AddLoginInfoActivityTest}.
 */
public class AddEditLoginInfoActivityTest {

    private Context context;
    private TextInputLayout inputLayout;
    private EditText editText;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        context.setTheme(R.style.AppTheme);

        inputLayout = new TextInputLayout(context);
        editText = new EditText(context);
        inputLayout.addView(editText);

    }

    @Test
    public void showErrorMessageTest1() {
        AddEditLoginInfoActivity.showErrorMessage(editText);

        String errorText = context.getString(R.string.error_text);

        assertNotNull(inputLayout.getError());
        assertEquals(errorText, inputLayout.getError());
    }

    @Test
    public void showErrorMessageTest2() {
        int errorTextResId = R.string.error_save_validation;
        String errorText = context.getString(errorTextResId);

        AddEditLoginInfoActivity.showErrorMessage(editText, errorTextResId);

        assertNotNull(inputLayout.getError());
        assertEquals(errorText, inputLayout.getError());
    }


    @Test
    public void showErrorMessageTest3() {
        String errorText = "new error message";

        AddEditLoginInfoActivity.showErrorMessage(editText, errorText);

        assertNotNull(inputLayout.getError());
        assertEquals(errorText, inputLayout.getError());
    }


    @Test
    public void clearErrorMessageTest() {
        showErrorMessageTest3();

        AddEditLoginInfoActivity.clearErrorMessage(editText);

        assertNull(inputLayout.getError());
    }


    @Test
    public void getParentTextInputLayoutSuccessfulTest() {
        AddEditLoginInfoActivity.getParentTextInputLayout(editText);
    }

    @Test(expected = NullPointerException.class)
    public void getParentTextInputLayoutNoParentTest() {
        EditText editTextWithNoParent = new EditText(context);
        AddEditLoginInfoActivity.getParentTextInputLayout(editTextWithNoParent);
    }

    @Test(expected = IllegalStateException.class)
    public void getParentTextInputLayoutWrongParentTest() {

        RelativeLayout relativeLayout = new RelativeLayout(context);
        EditText editTextWithWrongParent = new EditText(context);
        relativeLayout.addView(editTextWithWrongParent);

        AddEditLoginInfoActivity.getParentTextInputLayout(editTextWithWrongParent);
    }

    @Test
    public void isValidTextInputTest() {
        EditText editText = new EditText(context);
        editText.setText("Test");
        assertTrue(AddEditLoginInfoActivity.isValidTextInput(editText));

        editText.setText(null);
        assertFalse(AddEditLoginInfoActivity.isValidTextInput(editText));

        editText.setText("");
        assertFalse(AddEditLoginInfoActivity.isValidTextInput(editText));

        editText.setText("       ");
        assertFalse(AddEditLoginInfoActivity.isValidTextInput(editText));
    }

}