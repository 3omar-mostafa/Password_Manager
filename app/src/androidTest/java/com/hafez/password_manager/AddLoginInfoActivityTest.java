package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withChild;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAssertion;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * The {@link AddEditLoginInfoActivity} Class Test is separated into three test classes, class for
 * general functions in {@link AddEditLoginInfoActivityTest} , class for Add part which is this
 * class, i.e. {@link AddLoginInfoActivityTest}.
 */
public class AddLoginInfoActivityTest {

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
            };
        }
    };

    @Rule
    public ActivityTestRule<AddEditLoginInfoActivity> activityRule = new ActivityTestRule<>(
            activityFactory, true, true);


    private LoginInfoRepository repository;
    private Context context;
    private AddEditLoginInfoActivity activity;

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
    public void successfulInsertionTest() {

        String sampleUsername = "sample_username";
        String samplePassword = "sample_password";

        onView(withId(R.id.username)).perform(typeText(sampleUsername));
        onView(withId(R.id.password)).perform(typeText(samplePassword));
        onView(withId(R.id.save)).perform(click());

        TestObserver<List<LoginInfo>> observer = new TestObserver<List<LoginInfo>>() {
            @Override
            public void onChangedBehaviour(List<LoginInfo> loginInfoList) {
                assertEquals(1, loginInfoList.size());
                LoginInfo loginInfo = loginInfoList.get(0);
                assertEquals(1, loginInfo.getId());
                assertEquals(sampleUsername, loginInfo.getUsername());
                assertEquals(samplePassword, loginInfo.getPassword());
            }
        };

        repository.getAllLoginInfoList().observeForever(observer);

        assertTrue(observer.isOnChangedCalled());

        repository.getAllLoginInfoList().removeObserver(observer);
    }


    @Test
    public void failedInsertionInvalidInputTest() throws InterruptedException {
        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);

        onView(withId(R.id.save)).perform(click());

        String error = context.getString(R.string.error_text);
        ViewAssertion hasErrorMessage = matches(CustomViewMatchers.hasErrorText(error));

        onView(withChild(withChild(withId(R.id.username)))).check(hasErrorMessage);
        onView(withChild(withChild(withId(R.id.password)))).check(hasErrorMessage);

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);
    }


    @Test
    public void failedInsertionInvalidUsernameTest() throws InterruptedException {
        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);

        onView(withId(R.id.password)).perform(typeText("password"));
        onView(withId(R.id.save)).perform(click());

        String error = context.getString(R.string.error_text);
        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(error)));

        onView(withChild(withChild(withId(R.id.password))))
                .check(matches(CustomViewMatchers.hasErrorText(null)));

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);
    }


    @Test
    public void failedInsertionInvalidPasswordTest() throws InterruptedException {
        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);

        onView(withId(R.id.username)).perform(typeText("username"));
        onView(withId(R.id.save)).perform(click());

        String error = context.getString(R.string.error_text);
        onView(withChild(withChild(withId(R.id.password))))
                .check(matches(CustomViewMatchers.hasErrorText(error)));

        onView(withChild(withChild(withId(R.id.username))))
                .check(matches(CustomViewMatchers.hasErrorText(null)));

        onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(isDisplayed()))
                .check(matches(withText(R.string.error_save_validation)));

        Thread.sleep(1000);

        assertEquals(State.RESUMED, activity.getLifecycle().getCurrentState());

        DatabaseTestUtils.assertThatDatabaseIsEmpty(repository);
    }


}