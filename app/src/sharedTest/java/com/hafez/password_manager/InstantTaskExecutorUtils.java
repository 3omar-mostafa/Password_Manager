package com.hafez.password_manager;

import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.arch.core.executor.TaskExecutor;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class that swaps the background executor used by the Architecture Components with a
 * different one which executes each task synchronously.
 * <p>
 * You can use this rule for your host side tests that use Architecture Components.
 * <p>
 * Code is based on {@link InstantTaskExecutorRule}, but differs that is can be switched on or off
 * manually. This is useful when you have {@link LiveData} in your test that you want it to execute
 * sequentially and other {@link LiveData} in the main program that you want to leave it as it is.
 *
 * <pre>
 * Example usage :
 *
 * InstantTaskExecutorUtils.turnOn();
 * liveData.observeForever(observer);
 * InstantTaskExecutorUtils.turnOff();
 */
public class InstantTaskExecutorUtils {

    public static void turnOn() {
        ArchTaskExecutor.getInstance().setDelegate(new TaskExecutor() {
            @Override
            public void executeOnDiskIO(@NotNull Runnable runnable) {
                runnable.run();
            }

            @Override
            public void postToMainThread(@NotNull Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean isMainThread() {
                return true;
            }
        });
    }

    public static void turnOff() {
        ArchTaskExecutor.getInstance().setDelegate(null);
    }

}
