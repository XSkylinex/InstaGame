package com.example.test.contollers.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.models.Post;
import com.example.test.models.listener.Listener;
import com.example.test.models.listener.ListenerFirebaseAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PostAPI {

    PostAPI(){}

    private static CollectionReference postsCollection = FirebaseFirestore.getInstance()
            .collection("posts");

    public String generatePostId(){
        return postsCollection.document().getId();
    }

    public void addPost(Post post, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(post.get_id()).set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onComplete.accept(null);
                        }else{
                            onFailure.accept(task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void getPost(String postId, Consumer<Post> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(postId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Post post = documentSnapshot.toObject(Post.class);
                        onComplete.accept(post);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure.accept(e);
                    }
                });
    }


    public Listener listenPost(String postId, Consumer<Post> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = postsCollection.document(postId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Post post = snapshot.toObject(Post.class);
                    onComplete.accept(post);
                } else {
                    onComplete.accept(null);
                }
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenPosts(Consumer<List<Post>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = postsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Post> posts = new ArrayList<>();
                for (DocumentSnapshot doc : documents) {
                    posts.add(doc.toObject(Post.class));
                }
                onComplete.accept(posts);
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenPostsChanges(Consumer<Post> onAdded, Consumer<Post> onModified, Consumer<Post> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = postsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges) {
                    Post post = documentChange.getDocument().toObject(Post.class);
                    switch (documentChange.getType()) {
                        case ADDED:
                            onAdded.accept(post);
                            break;
                        case MODIFIED:
                            onModified.accept(post);
                            break;
                        case REMOVED:
                            onRemoved.accept(post);
                            break;
                    }
                }
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }



    public void getPosts(Consumer<List<Post>> onComplete, Consumer<Exception> onFailure) {
        postsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Post> posts = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    posts.add(document.toObject(Post.class));
                }
                onComplete.accept(posts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void deletePost(String postId, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(postId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onComplete.accept(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void updatePost(Post post, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(post.get_id()).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onComplete.accept(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });

    }

    public Listener listenPostsFromUser(String userId, Consumer<List<Post>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                postsCollection.whereEqualTo("_userId",userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Post> posts = new ArrayList<>();
                for (DocumentSnapshot doc : documents) {
                    posts.add(doc.toObject(Post.class));
                }
                onComplete.accept(posts);
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenPostsChangesFromUser(String userId,Consumer<Post> onAdded, Consumer<Post> onModified, Consumer<Post> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                postsCollection.whereEqualTo("_userId",userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges) {
                    Post post = documentChange.getDocument().toObject(Post.class);
                    switch (documentChange.getType()) {
                        case ADDED:
                            onAdded.accept(post);
                            break;
                        case MODIFIED:
                            onModified.accept(post);
                            break;
                        case REMOVED:
                            onRemoved.accept(post);
                            break;
                    }
                }
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }



    public void getPostsFromUser(String userId,Consumer<List<Post>> onComplete, Consumer<Exception> onFailure) {
        postsCollection.whereEqualTo("_userId",userId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Post> posts = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    posts.add(document.toObject(Post.class));
                }
                onComplete.accept(posts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

}
