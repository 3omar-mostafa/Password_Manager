package com.hafez.password_manager.Utils;

import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.Matchers.is;

import android.view.View;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomViewMatchers {

    public static Matcher<View> hasErrorText(final String expectedError) {
        return new HasErrorTextMatcher(checkNotNull(is(expectedError)));
    }

    public static class HasErrorTextMatcher extends BaseMatcher<View> {

        private Matcher<String> stringMatcher;

        private HasErrorTextMatcher(Matcher<String> stringMatcher) {
            this.stringMatcher = stringMatcher;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("with error: ");
            stringMatcher.describeTo(description);
        }

        @Override
        public boolean matches(Object item) {
            try {
                Method method = item.getClass().getMethod("getError");
                String e = (String) method.invoke(item);
                return stringMatcher.matches(e);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                return false;
            }
        }
    }


}
