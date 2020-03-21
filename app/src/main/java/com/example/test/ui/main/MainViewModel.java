package com.example.test.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<String>> users;
    public LiveData<List<String>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<List<String>>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.
        List<String> ausers = new ArrayList<String>();
        ausers.add("alex");
        ausers.add("anna");
        users.setValue(ausers);
    }
}
