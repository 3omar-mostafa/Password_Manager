package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
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
                    new Handler().postDelayed(super::finish, 1000);
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

    @Before
    public void init() {
        context = ApplicationProvider.getApplicationContext();

        initialLoginInfo = new LoginInfo(1, "old_username", "old_password", 0);

        LoginInfoDao loginInfoDao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
        repository = new DatabaseRepository(loginInfoDao);

        repository.insert(initialLoginInfo);

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


    @Test
    public void initialDataLoadedSuccessfullyTest() {
        onView(ViewMatchers.withId(R.id.username))
                .check(matches(ViewMatchers.withText(initialLoginInfo.getUsername())));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(ViewMatchers.withText(initialLoginInfo.getPassword())));
    }


    @Test
    public void updateUsernameTest() {

        String newUsername = "new_username";

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(newUsername));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), newUsername, initialLoginInfo.getPassword());
    }


    @Test
    public void updatePasswordTest() {

        String newPassword = "new_password";

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(newPassword));

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        assertNoErrors();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(), newPassword);
    }

    @Test
    public void updateUsernameAndPasswordTest() {

        String newUsername = "new_username";
        String newPassword = "new_password";

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(newUsername));

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText(newPassword));

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

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(),
                initialLoginInfo.getPassword());
    }

    @Test
    public void pressBackWithNoChangesTest() {

        Espresso.pressBackUnconditionally();

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(),
                initialLoginInfo.getPassword());
    }

    @Test
    public void pressUpWithNoChangesTest() {

        onView(ViewMatchers.withContentDescription(R.string.abc_action_bar_up_description))
                .perform(ViewActions.click());

        assertDatabaseEquals(initialLoginInfo.getId(), initialLoginInfo.getUsername(),
                initialLoginInfo.getPassword());
    }

    @Test
    public void updateInvalidInputTest() throws InterruptedException {
        long initialDatabaseSize = getLoginInfoTableSize();

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        String error = context.getString(R.string.error_text);

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        long currentDatabaseSize = getLoginInfoTableSize();
        assertEquals(initialDatabaseSize, currentDatabaseSize);
    }


    @Test
    public void updateInvalidUsernameTest() throws InterruptedException {
        long initialDatabaseSize = getLoginInfoTableSize();

        onView(ViewMatchers.withId(R.id.username))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        long currentDatabaseSize = getLoginInfoTableSize();
        assertEquals(initialDatabaseSize, currentDatabaseSize);
    }


    @Test
    public void updateInvalidPasswordTest() throws InterruptedException {
        long initialDatabaseSize = getLoginInfoTableSize();

        onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText());

        onView(ViewMatchers.withId(R.id.save))
                .perform(ViewActions.click());

        String error = context.getString(R.string.error_text);
        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasErrorText(error)));

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()))
                .check(matches(ViewMatchers.withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        long currentDatabaseSize = getLoginInfoTableSize();
        assertEquals(initialDatabaseSize, currentDatabaseSize);
    }


    private void assertNoErrors() {

        onView(ViewMatchers.withId(R.id.username))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(R.id.password))
                .check(matches(CustomViewMatchers.parentHasNoErrorText()));

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(doesNotExist());
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