package com.hafez.password_manager;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class RetryFailedTestsRule implements TestRule {

    private int retryCount;

    public RetryFailedTestsRule(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Throwable caughtThrowable = null;

                for (int i = 0; i < retryCount; i++) {
                    try {
                        base.evaluate();
                        return;
                    } catch (Throwable t) {
                        caughtThrowable = t;
                    }
                }

                throw caughtThrowable;
            }
        };

    }

}

