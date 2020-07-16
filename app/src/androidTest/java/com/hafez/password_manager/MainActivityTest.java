package com.hafez.password_manager;

import static androidx.test.espresso.Espresso.onView;
import static com.hafez.password_manager.AddEditLoginInfoActivity.ARGUMENT_LOGIN_INFO_ID;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
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
        for (int i = 0; i < 15; i++) {
            repository.insert(new LoginInfo("username#" + i, "password#" + i,
                    R.drawable.ic_launcher));
        }
    }


    private LoginInfoRepository repository;
    private List<LoginInfo> loginInfoDatabaseList;
    private MainActivity activity;

    @Before
    public void init() {

        fillDatabaseWithSampleData();

        InstantTaskExecutorUtils.turnOn();

        LiveData<List<LoginInfo>> liveData = repository.getAllLoginInfoList();
        loginInfoDatabaseList = LiveDataUtils.getValueOf(liveData);

        InstantTaskExecutorUtils.turnOff();

        activity = activityRule.getActivity();

        Intents.init();
    }

    @After
    public void cleanup() {
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

        long id = loginInfoDatabaseList.get(position).getId();
        Intents.intended(IntentMatchers.hasExtra(ARGUMENT_LOGIN_INFO_ID, id));

    }


    @Test
    public void clickLastItemInRecyclerViewAndVerifyIntentTest() {

        final int position = loginInfoDatabaseList.size() - 1;

        // Replaces Intent to open AddEditLoginInfoActivity with mock
        Intents.intending(IntentMatchers.hasComponent(AddEditLoginInfoActivity.class.getName()))
                .respondWith(new ActivityResult(Activity.RESULT_OK, null));

        onView(ViewMatchers.withId(R.id.login_info_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition(position));

        onView(ViewMatchers.withId(R.id.login_info_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.click()));

        long id = loginInfoDatabaseList.get(position).getId();
        Intents.intended(IntentMatchers.hasExtra(ARGUMENT_LOGIN_INFO_ID, id));

    }
}
