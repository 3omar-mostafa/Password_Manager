package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;
import com.hafez.password_manager.Utils.CustomViewMatchers;
import com.hafez.password_manager.database.DatabaseTestUtils;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The {@link AddEditLoginInfoActivity} Class Test is separated into three test classes, class for
 * general functions in {@link AddEditLoginInfoActivityTest} , class for Add part in {@link
 * AddLoginInfoActivityTest} , and class for Edit part which is this class, i.e. {@link
 * EditLoginInfoActivityTest}.
 */
public class EditLoginInfoActivityTest {

    @Rule(order = Integer.MIN_VALUE)
    public RetryFailedTestsRule retryFailedTestsRule = new RetryFailedTestsRule(5);

    // Used to inject view model before onCreate method in the activity, and delay finish activity
    private SingleActivityFactory<AddEditLoginInfoActivity> activityFactory = new SingleActivityFactory<AddEditLoginInfoActivity>(
            AddEditLoginInfoActivity.class) {
        @Override
        protected AddEditLoginInfoActivity create(Intent intent) {
            return new AddEditLoginInfoActivity() {
                @Override
                protected AddEditLoginInfoViewModel getViewModel(Factory factory) {
                    return new AddEditLoginInfoViewModel(repository, initialLoginInfo.getId());
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
            activityFactory, true, false);


    private LoginInfoRepository repository;
    private Context context;
    private AddEditLoginInfoActivity activity;
    private LoginInfo initialLoginInfo;
    long initialDatabaseSize;

    private static final String newUsername = "new_username";
    private static final String newPassword = "new_password";

    private static final int DIALOG_SAVE_BUTTON = android.R.id.button1;
    private static final int DIALOG_DISCARD_BUTTON = android.R.id.button2;

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        initialLoginInfo = new LoginInfo(1, "old_username", "old_password", 0);

        LoginInfoDao loginInfoDao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
        repository = new DatabaseRepository(loginInfoDao);

        repository.insert(initialLoginInfo);
        initialDatabaseSize = getLoginInfoTableSize();

        Intent intent = new Intent(context, AddEditLoginInfoActivity.class);
        intent.putExtra(AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID, initialLoginInfo.getId());
        activityRule.launchActivity(intent);

        activity = activityRule.getActivity();
    }

    @After
    public void cleanup() {
        if (activity != null) {
            activityRule.finishActivity();
        }
    }

    @After
    public void checkDatabaseSizeNotChanged() {
        long currentLoginInfoTableSize = getLoginInfoTableSize();
        assertEquals(initialDatabaseSize, currentLoginInfoTableSize);
    }

    @Test
    public void initialDataLoadedSuccessfullyTest() {

        String title = context.getString(R.string.edit_login_info);

        assertEquals(title, activity.getTitle());

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(ViewMatchers.withText(initialLoginInfo.getUsername())));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(ViewMatchers.withText(initialLoginInfo.getPassword())));
    }


    @Test
    public void updateUsernameTest() {

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, initialLoginInfo.getPassword());
    }


    @Test
    public void updatePasswordTest() {

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(), newPassword);
    }

    @Test
    public void updateUsernameAndPasswordTest() {

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, newPassword);
    }

    @Test
    public void saveWithNoChangesTest() {

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseNotChanged();
    }

    @Test
    public void updateInvalidInputTest() {

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkFailedToUpdate_InvalidUsernameAndPassword();
    }

    private void checkFailedToUpdate_InvalidUsernameAndPassword() {
        String error = context.getString(R.string.error_text);

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        assertDatabaseNotChanged();
    }


    @Test
    public void updateInvalidUsernameTest() {

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkFailedToUpdate_InvalidUsername();
    }

    private void checkFailedToUpdate_InvalidUsername() {
        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        assertDatabaseNotChanged();
    }


    @Test
    public void updateInvalidPasswordTest() {

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        checkFailedToUpdate_InvalidPassword();
    }

    private void checkFailedToUpdate_InvalidPassword() {
        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        assertDatabaseNotChanged();
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
    public void pressBack_UpdatedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, initialLoginInfo.getPassword());
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedUsername_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, initialLoginInfo.getPassword());
    }

    @Test
    public void pressBack_UpdatedUsername_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedUsername_Discard_Test} with changing back button to
     * up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedUsername_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }


    @Test
    public void pressBack_DeletedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsername();
    }

    /**
     * Duplicated from {@link #pressBack_DeletedUsername_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_DeletedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsername();
    }


    @Test
    public void pressBack_UpdatedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(), newPassword);
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedPassword_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(), newPassword);
    }

    @Test
    public void pressBack_UpdatedPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedPassword_Discard_Test} with changing back button to
     * up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }

    @Test
    public void pressBack_DeletedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidPassword();
    }

    /**
     * Duplicated from {@link #pressBack_DeletedPassword_Save_Test} with changing back button to up
     * button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_DeletedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidPassword();
    }


    @Test
    public void pressBack_UpdatedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, newPassword);
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedUsernameAndPassword_Save_Test} with changing back
     * button to up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, newPassword);
    }

    @Test
    public void pressBack_UpdatedUsernameAndPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }

    /**
     * Duplicated from {@link #pressBack_UpdatedUsernameAndPassword_Discard_Test} with changing back
     * button to up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_UpdatedUsernameAndPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }


    @Test
    public void pressBack_DeletedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressBackAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsernameAndPassword();
    }

    /**
     * Duplicated from {@link #pressBack_DeletedUsernameAndPassword_Save_Test} with changing back
     * button to up button because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_DeletedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsernameAndPassword();
    }


    @Test
    public void pressBack_NoChanges_Test() {
        Espresso.closeSoftKeyboard();

        Espresso.pressBackUnconditionally();

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());

        assertDatabaseNotChanged();
    }

    /**
     * Duplicated from {@link #pressBack_NoChanges_Test} with changing back button to up button
     * because Android Test Orchestrator does not support parameterized tests
     */
    @Test
    public void pressUp_NoChanges_Test() {
        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                .perform(ViewActions.click());

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());

        assertDatabaseNotChanged();
    }


    private void assertNoErrors() {

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(doesNotExist());
    }


    private void assertDatabaseNotChanged() {
        long currentLoginInfoTableSize = getLoginInfoTableSize();

        assertEquals(initialDatabaseSize, currentLoginInfoTableSize);

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(),
                initialLoginInfo.getPassword());
    }

    private void assertDatabaseEquals(long id, String username, String password) {
        InstantTaskExecutorUtils.turnOn();

        final int databaseSize = 1;

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();

        List<LoginInfo> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertEquals(databaseSize, loginInfoList.size());
        LoginInfo loginInfo = loginInfoList.get(0);
        assertEquals(id, loginInfo.getId());
        assertEquals(username, loginInfo.getUsername());
        assertEquals(password, loginInfo.getPassword());

        InstantTaskExecutorUtils.turnOff();
    }

    private long getLoginInfoTableSize() {
        InstantTaskExecutorUtils.turnOn();
        long size = DatabaseTestUtils.getLoginInfoTableSize(repository);
        InstantTaskExecutorUtils.turnOff();
        return size;
    }

}