package com.example.test.models.listener;

import com.google.firebase.firestore.ListenerRegistration;

public class ListenerFirebaseAdapter implements Listener {

    ListenerRegistration listenerRegistration;

    public ListenerFirebaseAdapter(ListenerRegistration listenerRegistration) {
        this.listenerRegistration = listenerRegistration;
    }

    @Override
    public void remove() {
        listenerRegistration.remove();
    }
}
