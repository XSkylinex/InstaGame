package com.example.test.ui.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.test.contollers.database.Database;
import com.example.test.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MainViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<List<String>> users;
    //real time live data from the server
    public LiveData<List<String>> getUsers() {
        if (users == null) {
            users = new MutableLiveData<List<String>>();
            loadUsers();
        }
        return users;
    }

    private void loadUsers() {
        // Do an asynchronous operation to fetch users.

        Database.User.getCurrentUser(new Consumer<User>() {
            @Override
            public void accept(User user) {
                List<String> ausers = new ArrayList<String>();
                ausers.add(user.get_userName());
                users.setValue(ausers);
            }
        }, new Consumer<Exception>() {
            @Override
            public void accept(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
