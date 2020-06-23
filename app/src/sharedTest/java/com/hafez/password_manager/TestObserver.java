package com.hafez.password_manager;

import androidx.lifecycle.Observer;

public abstract class TestObserver<T> implements Observer<T> {

    private boolean isCalled = false;

    public abstract void onChangedBehaviour(T t);

    @Override
    public void onChanged(T t) {
        onChangedBehaviour(t);
        isCalled = true;
    }

    public boolean isOnChangedCalled() {
        return isCalled;
    }

}
