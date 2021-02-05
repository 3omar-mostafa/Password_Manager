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
import com.hafez.password_manager.models.LoginInfoFull;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.AddEditLoginInfoViewModel;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

/**
 * This class test pressing up or back button in {@link EditLoginInfoActivityTest}
 */
@RunWith(Parameterized.class)
public class EditLoginInfoActivityBackOrUpParameterizedTest {

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
    private LoginInfoFull initialLoginInfo;
    long initialDatabaseSize;

    private static final String newUsername = "new_username";
    private static final String newPassword = "new_password";

    private static final int DIALOG_SAVE_BUTTON = android.R.id.button1;
    private static final int DIALOG_DISCARD_BUTTON = android.R.id.button2;

    interface PressBackOrUp {

        void press();
    }

    @Parameter
    public PressBackOrUp pressBackOrUp;

    @Parameters
    public static PressBackOrUp[] data() {
        return new PressBackOrUp[]{
                Espresso::pressBackUnconditionally, // Press Back
                () -> onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                        .perform(ViewActions.click()) // Press Up
        };
    }

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        initialLoginInfo = new LoginInfoFull(1, "old_username", "old_password");

        LoginInfoDao loginInfoDao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
        repository = new DatabaseRepository(loginInfoDao);

        repository.insert(initialLoginInfo);
        initialDatabaseSize = getLoginInfoTableSize();

        Intent intent = new Intent(context, AddEditLoginInfoActivity.class);
        intent.putExtra(AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID,
                initialLoginInfo.getId());
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

    private void pressBackOrUpAndCheckDialogIsDisplayedAndClick(@IdRes int button) {
        Espresso.closeSoftKeyboard();

        pressBackOrUp.press();

        onView(ViewMatchers.withText(R.string.unsaved_changes)).inRoot(RootMatchers.isDialog())
                .check(matches(ViewMatchers.isDisplayed()));

        onView(ViewMatchers.withId(button)).inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click());
    }


    @Test
    public void pressBackOrUp_UpdatedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername,
                initialLoginInfo.getPassword());
    }

    @Test
    public void pressBackOrUp_UpdatedUsername_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }


    @Test
    public void pressBackOrUp_DeletedUsername_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsername();
    }

    @Test
    public void pressBackOrUp_UpdatedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(), newPassword);
    }


    @Test
    public void pressBackOrUp_UpdatedPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }


    @Test
    public void pressBackOrUp_DeletedPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidPassword();
    }


    @Test
    public void pressBackOrUp_UpdatedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, newPassword);
    }

    @Test
    public void pressBackOrUp_UpdatedUsernameAndPassword_Discard_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.replaceText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.replaceText(newPassword));

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_DISCARD_BUTTON);

        assertDatabaseNotChanged();
    }


    @Test
    public void pressBackOrUp_DeletedUsernameAndPassword_Save_Test() {
        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        pressBackOrUpAndCheckDialogIsDisplayedAndClick(DIALOG_SAVE_BUTTON);

        checkFailedToUpdate_InvalidUsernameAndPassword();
    }

    @Test
    public void pressBackOrUp_NoChanges_Test() {
        Espresso.closeSoftKeyboard();

        pressBackOrUp.press();

        onView(ViewMatchers.withText(R.string.unsaved_changes))
                .check(doesNotExist());

        assertDatabaseNotChanged();
    }


    protected void checkFailedToUpdate_InvalidUsernameAndPassword() {
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

    protected void checkFailedToUpdate_InvalidUsername() {
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

    protected void checkFailedToUpdate_InvalidPassword() {
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

    protected void assertNoErrors() {

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(doesNotExist());
    }

    protected void assertDatabaseNotChanged() {
        long currentLoginInfoTableSize = getLoginInfoTableSize();

        assertEquals(initialDatabaseSize, currentLoginInfoTableSize);

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(),
                initialLoginInfo.getPassword());
    }

    protected void assertDatabaseEquals(long id, String username, String password) {
        InstantTaskExecutorUtils.turnOn();

        final int databaseSize = 1;

        LiveData<List<LoginInfoFull>> liveData = repository.getAllLoginInfoList();

        List<LoginInfoFull> loginInfoList = LiveDataUtils.getValueOf(liveData);

        assertEquals(databaseSize, loginInfoList.size());
        LoginInfoFull loginInfo = loginInfoList.get(0);
        assertEquals(id, loginInfo.getId());
        assertEquals(username, loginInfo.getUsername());
        assertEquals(password, loginInfo.getPassword());

        InstantTaskExecutorUtils.turnOff();
    }

    protected long getLoginInfoTableSize() {
        InstantTaskExecutorUtils.turnOn();
        long size = DatabaseTestUtils.getLoginInfoTableSize(repository);
        InstantTaskExecutorUtils.turnOff();
        return size;
    }

}
