package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import com.google.android.material.textfield.TextInputLayout;
import com.hafez.password_manager.Utils.CustomViewMatchers;
import com.hafez.password_manager.database.DatabaseTestUtils;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The {@link AddEditLoginInfoActivity} Class Test is separated into three test classes, class for
 * general functions which is this class, i.e. {@link AddEditLoginInfoActivityTest}  , class for Add
 * part which is this in {@link AddLoginInfoActivityTest}.
 */
public class AddEditLoginInfoActivityTest {

    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    @Rule
    public ActivityScenarioRule<AddEditLoginInfoActivity> activityScenarioRule =
            new ActivityScenarioRule<>(AddEditLoginInfoActivity.class);

    private ActivityScenario<AddEditLoginInfoActivity> activityScenario;
    private Context context;


    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        activityScenario = activityScenarioRule.getScenario().onActivity(activity -> {
            activity.viewModel = getViewModelWithInMemoryDatabase();
        });
    }

    @NonNull
    private AddEditLoginInfoViewModel getViewModelWithInMemoryDatabase() {
        LoginInfoDao dao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
        return new AddEditLoginInfoViewModel(new DatabaseRepository(dao));
    }

    @Test
    public void showErrorMessageTest1() {
        activityScenario.onActivity(activity -> {
            activity.showErrorMessage(activity.viewBinding.username);
        });

        String errorText = context.getString(R.string.error_text);
        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(errorText)));
    }

    @Test
    public void showErrorMessageTest2() {
        int errorTextResId = R.string.error_save_validation;

        activityScenario.onActivity(activity -> {
            activity.showErrorMessage(activity.viewBinding.username, errorTextResId);
        });

        String errorText = context.getString(errorTextResId);
        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(errorText)));
    }


    @Test
    public void showErrorMessageTest3() {
        String errorText = "new error message";

        activityScenario.onActivity(activity -> {
            activity.showErrorMessage(activity.viewBinding.username, errorText);
        });

        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(errorText)));
    }


    @Test
    public void clearErrorMessageTest() {
        showErrorMessageTest3();

        activityScenario.onActivity(activity -> {
            activity.clearErrorMessage(activity.viewBinding.username);
        });

        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(null)));
    }


    @Test
    public void getParentTextInputLayoutSuccessfulTest() {

        activityScenario.onActivity(activity -> {

            TextInputLayout inputLayout = new TextInputLayout(activity);
            EditText editText = new EditText(activity);
            inputLayout.addView(editText);

            activity.getParentTextInputLayout(editText);
        });
    }

    @Test(expected = NullPointerException.class)
    public void getParentTextInputLayoutNoParentTest() {

        final AddEditLoginInfoActivity[] activityArray = new AddEditLoginInfoActivity[1];

        activityScenario.onActivity(activity -> activityArray[0] = activity);
        AddEditLoginInfoActivity activity = activityArray[0];

        EditText editText = new EditText(activity);
        activity.getParentTextInputLayout(editText);
    }

    @Test(expected = IllegalStateException.class)
    public void getParentTextInputLayoutWrongParentTest() {

        final AddEditLoginInfoActivity[] activityArray = new AddEditLoginInfoActivity[1];

        activityScenario.onActivity(activity -> activityArray[0] = activity);
        AddEditLoginInfoActivity activity = activityArray[0];

        RelativeLayout relativeLayout = new RelativeLayout(activity);
        EditText editText = new EditText(activity);
        relativeLayout.addView(editText);

        activity.getParentTextInputLayout(editText);
    }

    @Test
    public void isValidTextInputTest() {
        activityScenario.onActivity(activity -> {
            EditText editText = new EditText(activity);
            editText.setText("Test");
            assertTrue(activity.isValidTextInput(editText));

            editText.setText(null);
            assertFalse(activity.isValidTextInput(editText));
        });
    }

}