package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static com.hafez.password_manager.AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import android.os.SystemClock;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;
import com.hafez.password_manager.database.DatabaseTestUtils;
import com.hafez.password_manager.database.LoginInfoDao;
import com.hafez.password_manager.models.LoginInfo;
import com.hafez.password_manager.repositories.DatabaseRepository;
import com.hafez.password_manager.repositories.LoginInfoRepository;
import com.hafez.password_manager.view_models.LoginInfoViewModel;
import java.util.List;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {

    @Rule(order = Integer.MIN_VALUE)
    public RetryFailedTestsRule retryFailedTestsRule = new RetryFailedTestsRule(5);

    private SingleActivityFactory<MainActivity> activityFactory = new SingleActivityFactory<MainActivity>(
            MainActivity.class) {
        @Override
        protected MainActivity create(Intent intent) {
            return new MainActivity() {
                @Override
                protected LoginInfoViewModel getViewModel(Factory factory) {
                    LoginInfoDao dao = DatabaseTestUtils.getInMemoryDatabase().getLoginInfoDao();
                    repository = new DatabaseRepository(dao);
                    return new LoginInfoViewModel(repository);
                }
            };
        }
    };


    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(activityFactory,
            true, true);


    private void fillDatabaseWithSampleData() {
        idlingResource.increment();

        int size = 15;

        for (int i = 0; i < size; i++) {
            repository.insert(new LoginInfo("username#" + i, "password#" + i,
                    R.drawable.ic_launcher));
        }

        while (true) {
            SystemClock.sleep(1000);
            if (getLoginInfoListFromDatabase().size() == size) {
                idlingResource.decrement();
                break;
            }
        }

    }


    private List<LoginInfo> getLoginInfoListFromDatabase() {
        InstantTaskExecutorUtils.turnOn();

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();
        List<LoginInfo> list = LiveDataUtils.getValueOf(liveData);

        InstantTaskExecutorUtils.turnOff();

        return list;
    }

    private LoginInfoRepository repository;
    private List<LoginInfo> initialLoginInfoDatabaseList;
    private MainActivity activity;

    private CountingIdlingResource idlingResource = new CountingIdlingResource("Test");


    @Before
    public void init() {

        IdlingRegistry.getInstance().register(idlingResource);

        fillDatabaseWithSampleData();

        initialLoginInfoDatabaseList = getLoginInfoListFromDatabase();

        activity = activityRule.getActivity();

        Intents.init();
    }

    @After
    public void cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource);

        Intents.release();
    }

    @Test
    public void clickAddItemAndVerifyIntentTest() {
        // Replaces Intent to open AddEditLoginInfoActivity with mock
        Intents.intending(IntentMatchers.hasComponent(AddEditLoginInfoActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_OK, null));

        onView(ViewMatchers.withId(R.id.add))
                .perform(ViewActions.click());

        Matcher<Intent> hasNoExtras = Matchers
                .not(IntentMatchers.hasExtra(Matchers.any(String.class), Matchers.anything()));

        Intents.intended(hasNoExtras);
    }

    @Test
    public void clickFirstItemInRecyclerViewAndVerifyIntentTest() {

        final int position = 0;

        // Replaces Intent to open AddEditLoginInfoActivity with mock
        Intents.intending(IntentMatchers.hasComponent(AddEditLoginInfoActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_OK, null));

        onView(ViewMatchers.withId(R.id.login_info_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.click()));

        long id = initialLoginInfoDatabaseList.get(position).getId();
        Intents.intended(IntentMatchers.hasExtra(ARGUMENT_LOGIN_INFO_ID, id));

    }


    @Test
    public void clickLastItemInRecyclerViewAndVerifyIntentTest() {

        final int position = initialLoginInfoDatabaseList.size() - 1;

        // Replaces Intent to open AddEditLoginInfoActivity with mock
        Intents.intending(IntentMatchers.hasComponent(AddEditLoginInfoActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_OK, null));

        onView(ViewMatchers.withId(R.id.login_info_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.click()));

        long id = initialLoginInfoDatabaseList.get(position).getId();
        Intents.intended(IntentMatchers.hasExtra(ARGUMENT_LOGIN_INFO_ID, id));

    }

    private void swipeItemInRecyclerViewAndCheckItIsDeleted(int position, ViewAction swipeAction) {

        onView(ViewMatchers.withId(R.id.login_info_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, swipeAction));

        List<LoginInfo> loginInfoList = getLoginInfoListFromDatabase();

        assertEquals(initialLoginInfoDatabaseList.size() - 1, loginInfoList.size());
        assertFalse(loginInfoList.contains(initialLoginInfoDatabaseList.get(position)));
    }

    @Test
    public void swipeLeftToDeleteTest() {

        final int position = 5;

        swipeItemInRecyclerViewAndCheckItIsDeleted(position, ViewActions.swipeLeft());

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void swipeRightToDeleteTest() {

        final int position = initialLoginInfoDatabaseList.size() - 1;

        swipeItemInRecyclerViewAndCheckItIsDeleted(position, ViewActions.swipeRight());

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(ViewMatchers.isDisplayed()));

    }

    @Test
    public void undoDeleteTest() {

        int position = 1;

        swipeItemInRecyclerViewAndCheckItIsDeleted(position, ViewActions.swipeLeft());

        onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_action))
                .perform(ViewActions.click());

        List<LoginInfo> loginInfoList = getLoginInfoListFromDatabase();

        assertEquals(initialLoginInfoDatabaseList.size(), loginInfoList.size());
        assertTrue(loginInfoList.contains(initialLoginInfoDatabaseList.get(position)));

    }


}
