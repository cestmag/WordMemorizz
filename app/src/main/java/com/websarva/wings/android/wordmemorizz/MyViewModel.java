package com.websarva.wings.android.wordmemorizz;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    private MutableLiveData<Boolean> finishActivityA = new MutableLiveData<>();

    public void setFinishActivityA(boolean value) {
        finishActivityA.setValue(value);
    }

    public boolean getFinishActivityA() {
        Boolean value = finishActivityA.getValue();
        return value != null ? value : false;
    }
}
