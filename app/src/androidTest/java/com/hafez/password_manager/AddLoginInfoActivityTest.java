package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.MonitoringInstrumentation;
import androidx.test.runner.intercepting.SingleActivityFactory;
import com.hafez.password_manager.Utils.CustomViewMatchers;
import com.hafez.password_manager.database.DatabaseTestUtils;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The {@link AddEditLoginInfoActivity} Class Test is separated into three test classes, class for
 * general functions in {@link AddEditLoginInfoActivityTest} , class for Add part which is this
 * class, i.e. {@link AddLoginInfoActivityTest} , and class for Edit part in {@link
 * EditLoginInfoActivityTest}.
 */
public class AddLoginInfoActivityTest {

    @Rule(order = Integer.MIN_VALUE)
    public RetryFailedTestsRule retryFailedTestsRule = new RetryFailedTestsRule(5);

    // Allows Android Architecture Components (ex. Live Data) to executes each task synchronously.
    @Rule
    public InstantTaskExecutorRule executorRule = new InstantTaskExecutorRule();

    // Used to inject view model before onCreate method in the activity, and delay finish activity
    private SingleActivityFactory<AddEditLoginInfoActivity> activityFactory = new SingleActivityFactory<AddEditLoginInfoActivity>(
            AddEditLoginInfoActivity.class) {
        @Override
        protected AddEditLoginInfoActivity create(Intent intent) {
            return new AddEditLoginInfoActivity() {
                @Override
                protected AddEditLoginInfoViewModel getViewModel(Factory factory) {
                    return getViewModelWithInMemoryDatabase();
                }

                @Override
                public void finish() {
                    // Delay finish to have time to make some checks on activity
                    new Handler().postDelayed(super::finish, 3000);
                }

                @Override
                public boolean onSupportNavigateUp() {
                    // Delay finish to have time to make some checks on activity
                    new Handler().postDelayed(super::onSupportNavigateUp, 3000);

                    return true;
                }
            };
        }
    };

    @Rule
    public ActivityTestRule<AddEditLoginInfoActivity> activityRule = new ActivityTestRule<>(
            activityFactory, true, true);


    private LoginInfoRepository repository;
    private Context context;
    private AddEditLoginInfoActivity activity;

    private static final String sampleUsername = "sample_username";
    private static final String samplePassword = "sample_password";

    private static final int DIALOG_SAVE_BUTTON = android.R.id.button1;
    private static final int DIALOG_DISCARD_BUTTON = android.R.id.button2;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        activity = activityRule.getActivity();

    }

    @NonNull
    private AddEditLoginInfoViewModel getViewModelWithInMemoryDatabase() {
        LoginInfoDao loginInfoDao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
        repository = new DatabaseRepository(loginInfoDao);
        return new AddEditLoginInfoViewModel(repository);
    }


    @Test
    public void initialTest() {

        String title = context.getString(R.string.add_login_info);

        assertEquals(title, activity.getTitle());

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(ViewMatchers.withText("")));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(ViewMatchers.withText("")));
    }



    @Test
    public void successfulInsertionTest() {

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkValidInsertionToDatabase();
    }

    private void checkValidInsertionToDatabase() {
        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(doesNotExist());

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertEquals(1, loginInfoList.size());
        LoginInfo loginInfo = loginInfoList.get(0);
        assertEquals(1, loginInfo.getId());
        assertEquals(sampleUsername, loginInfo.getUsername());
        assertEquals(samplePassword, loginInfo.getPassword());
    }


    @Test
    public void failedInsertionInvalidInputTest() {
        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);

        onView(ViewMatchers.withId(R.id.save)).perform(ViewActions.click());

        String error = context.getString(R.string.error_text);

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));


        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }


    @Test
    public void failedInsertionInvalidUsernameTest() {
        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkFailedToInsert_InvalidUsername();
    }

    private void checkFailedToInsert_InvalidUsername() {
        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }


    @Test
    public void failedInsertionInvalidPasswordTest() {
        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkFailedToInsert_InvalidPassword();
    }

    private void checkFailedToInsert_InvalidPassword() {
        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    private void pressBackAndCheckDialogIsDisplayedAndClick(@IdRes int button) {
        Espresso.closeSoftKeyboard();

        Espresso.pressBackUnconditionally();

        onView(ViewMatchers.withText(R.string.unsaved_changes)).inRoot(RootMatchers.isDialog())
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(button)).inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click());
    }


    private void pressUpAndCheckDialogIsDisplayedAndClick(@IdRes int button) {
        Espresso.closeSoftKeyboard();

        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                .perform(ViewActions.click());

        onView(ViewMatchers.withText(R.string.unsaved_changes)).inRoot(RootMatchers.isDialog())
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(button)).inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click());
    }

    @Test
    public void pressBack_AddedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToInsert_InvalidPassword();
    }

    /**
     * Duplicated from {@link #pressBack_AddedUsername_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToInsert_InvalidPassword();
    }

    @Test
    public void pressBack_AddedUsername_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    /**
     * Duplicated from {@link #pressBack_AddedUsername_Discard_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedUsername_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    @Test
    public void pressBack_AddedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToInsert_InvalidUsername();
    }

    /**
     * Duplicated from {@link #pressBack_AddedPassword_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToInsert_InvalidUsername();
    }

    @Test
    public void pressBack_AddedPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    /**
     * Duplicated from {@link #pressBack_AddedPassword_Discard_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }


    @Test
    public void pressBack_AddedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkValidInsertionToDatabase();
    }

    /**
     * Duplicated from {@link #pressBack_AddedUsernameAndPassword_Save_Test} with changing back
     * button to up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkValidInsertionToDatabase();
    }

    @Test
    public void pressBack_AddedUsernameAndPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    /**
     * Duplicated from {@link #pressBack_AddedUsernameAndPassword_Discard_Test} with changing back
     * button to up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedUsernameAndPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText(samplePassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        DatabaseTestUtils.assertThatLoginInfoTableIsEmpty(repository);
    }

    @Test
    public void pressBack_NoChanges_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername))
                .perform(ViewActions.clearText());

        Espresso.closeSoftKeyboard();

        Espresso.pressBackUnconditionally();

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());
    }

    /**
     * Duplicated from {@link #pressBack_NoChanges_Test} with changing back button to up button
     * because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_NoChanges_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText(sampleUsername))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                .perform(ViewActions.click());

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());
    }


    @Test
    public void pressBack_AddedWhitespaces_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText("  "));

        Espresso.closeSoftKeyboard();

        Espresso.pressBackUnconditionally();

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());

    }

    /**
     * Duplicated from {@link #pressBack_AddedWhitespaces_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_AddedWhitespaces_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.typeText("  "));

        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                .perform(ViewActions.click());

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());

    }


}