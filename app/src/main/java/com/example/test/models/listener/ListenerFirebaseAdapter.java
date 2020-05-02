package com.example.test.models.listener;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.ListenerRegistration;

public class ListenerFirebaseAdapter implements Listener {

    @NonNull
    private final ListenerRegistration listenerRegistration;

    public ListenerFirebaseAdapter(@NonNull ListenerRegistration listenerRegistration) {
        this.listenerRegistration = listenerRegistration;
    }

    @Override
    public void remove() {
        listenerRegistration.remove();
    }
}
