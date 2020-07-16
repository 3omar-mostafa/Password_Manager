package com.hafez.password_manager.Utils;

import static androidx.test.internal.util.Checks.checkNotNull;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.test.espresso.matcher.BoundedMatcher;
import com.google.android.material.textfield.TextInputLayout;
import com.hafez.password_manager.AddEditLoginInfoActivity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class CustomViewMatchers {

    public static Matcher<View> hasErrorText(@NonNull final String expectedError) {
        return new HasErrorTextMatcher(checkNotNull(is(expectedError)));
    }

    public static Matcher<View> hasNoErrorText() {
        return new HasErrorTextMatcher(null);
    }

    public static Matcher<View> parentHasErrorText(@NonNull final String expectedError) {
        return new ParentTextInputLayoutHasErrorTextMatcher(checkNotNull(is(expectedError)));
    }

    public static Matcher<View> parentHasNoErrorText() {
        return new ParentTextInputLayoutHasErrorTextMatcher(null);
    }

    public static class HasErrorTextMatcher extends BaseMatcher<View> {

        private Matcher<String> stringMatcher;

        private HasErrorTextMatcher(Matcher<String> stringMatcher) {
            this.stringMatcher = stringMatcher;
        }

        @Override
        public void describeTo(Description description) {
            if (stringMatcher == null) {
                description.appendText("View Contains No Errors");
            } else {
                description.appendText("with error: ");
                stringMatcher.describeTo(description);
            }
        }

        @Override
        public boolean matches(Object item) {
            try {
                Method method = item.getClass().getMethod("getError");
                String e = (String) method.invoke(item);

                // If StringMatcher is null (i.e. no error) then getError should also return null
                if (stringMatcher == null) {
                    return e == null;
                }

                return stringMatcher.matches(e);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                return false;
            }
        }
    }


    public static class ParentTextInputLayoutHasErrorTextMatcher extends BoundedMatcher<View, EditText> {

        private Matcher<String> stringMatcher;

        private ParentTextInputLayoutHasErrorTextMatcher(Matcher<String> stringMatcher) {
            super(EditText.class);
            this.stringMatcher = stringMatcher;
        }

        @Override
        public void describeTo(Description description) {
            if (stringMatcher == null) {
                description.appendText("View Contains No Errors");
            } else {
                description.appendText("with error: ");
                stringMatcher.describeTo(description);
            }
        }

        @Override
        protected boolean matchesSafely(EditText item) {
            try {
                TextInputLayout parentTextInputLayout = getParentTextInputLayout(item);

                String error = (String) parentTextInputLayout.getError();

                // If StringMatcher is null (i.e. no error) then getError should also return null
                if (stringMatcher == null) {
                    return error == null;
                }

                return stringMatcher.matches(error);

            } catch (Exception e) {
                return false;
            }
        }

        private static TextInputLayout getParentTextInputLayout(@NonNull EditText editText)
                throws IllegalStateException {
            TextInputLayout inputLayout;

            // Uses parent of parent because TextInputLayout puts a FrameLayout as a direct child by default
            ViewParent parent = editText.getParent().getParent();
            if (parent instanceof TextInputLayout) {
                inputLayout = (TextInputLayout) parent;
            } else {
                throw new IllegalStateException("Parent View Must be TextInputLayout");
            }
            return inputLayout;
        }

    }



}
