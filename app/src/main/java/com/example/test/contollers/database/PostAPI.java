package com.example.test.contollers.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.example.test.models.Post;
import com.example.test.models.listener.Listener;
import com.example.test.models.listener.ListenerFirebaseAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostAPI {

    final static String LIKES = "likes";
    final static String POSTS = "posts";

    PostAPI(){}

    static CollectionReference postsCollection = FirebaseFirestore.getInstance()
            .collection(POSTS);

    public String generatePostId(){
        return postsCollection.document().getId();
    }

    public void addPost(Post post, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(post.get_id()).set(post)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onComplete.accept(null);
                    }else{
                        onFailure.accept(task.getException());
                    }
                }).addOnFailureListener(onFailure::accept);
    }

    public void getPost(String postId, Consumer<Post> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(postId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Post post = documentSnapshot.toObject(Post.class);
                    onComplete.accept(post);
                })
                .addOnFailureListener(onFailure::accept);
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
        ListenerRegistration listenerRegistration = postsCollection.addSnapshotListener((queryDocumentSnapshots, e) -> {
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
        }).addOnFailureListener(onFailure::accept);
    }

    public void deletePost(String postId, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        WriteBatch batch = FirebaseFirestore.getInstance().batch();
        batch.delete(postsCollection.document(postId));
        postsCollection.document(postId).collection(LIKES).get()
                .addOnSuccessListener(queryDocumentSnapshots ->{
                    queryDocumentSnapshots.getDocuments().forEach(documentSnapshot ->
                            batch.delete(postsCollection.document(postId).collection(LIKES).document(documentSnapshot.getId())));

                    postsCollection.document(postId).collection(CommentAPI.Comments).get().addOnSuccessListener(queryDocumentSnapshots1 ->{
                        queryDocumentSnapshots1.getDocuments().forEach(documentSnapshot ->
                                batch.delete(postsCollection.document(postId).collection(LIKES).document(documentSnapshot.getId())));
                        batch.commit().addOnSuccessListener(onComplete::accept).addOnFailureListener(onFailure::accept);

                    }).addOnFailureListener(onFailure::accept);

                }).addOnFailureListener(onFailure::accept);
    }

    public void updatePost(Post post, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(post.get_id()).set(post)
                .addOnSuccessListener(aVoid -> onComplete.accept(null)).addOnFailureListener(onFailure::accept);

    }

    public Listener listenPostsFromUser(String userId, Consumer<List<Post>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                postsCollection.whereEqualTo("_userId",userId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
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
                        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenPostsChangesFromUser(String userId,Consumer<Post> onAdded, Consumer<Post> onModified, Consumer<Post> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                postsCollection.whereEqualTo("_userId",userId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
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
        }).addOnFailureListener(onFailure::accept);
    }

    public void isPostExist(String postId, Consumer<Boolean> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(postId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                onComplete.accept(documentSnapshot.exists());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }


    public void runTransactionLike(String postId, String userId,Consumer<Integer> onComplete, Consumer<Exception> onFailure){
        final DocumentReference postRef = postsCollection.document(postId);
        final DocumentReference likeUserRef = postsCollection.document(postId).collection(LIKES).document(userId);
        FirebaseFirestore.getInstance().runTransaction(transaction -> {
            DocumentSnapshot snapshot_postRef = transaction.get(postRef);
            DocumentSnapshot snapshot_likeUserRef = transaction.get(likeUserRef);
            if(!snapshot_postRef.exists()){
                throw new FirebaseFirestoreException("Post isn't exist",
                        FirebaseFirestoreException.Code.NOT_FOUND);
            }
            int num = 0;
            if (snapshot_likeUserRef.exists()){
                likeUserRef.delete();
                num = -1;
            }else{
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put(userId,true);
                likeUserRef.set(hashMap);
                num = 1;
            }


            // Success
            return num;
        }).addOnSuccessListener(num -> {
            Log.d("PostAPI", "Transaction success!");
            onComplete.accept(num);
        })
        .addOnFailureListener(e -> {
            Log.w("PostAPI", "Transaction failure.", e);
            onFailure.accept(e);
        });



    }

    public void isLiked(String postId, String userId,Consumer<Boolean> onComplete, Consumer<Exception> onFailure){
        postsCollection.document(postId).collection(LIKES).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> onComplete.accept(documentSnapshot.exists()))
                .addOnFailureListener(onFailure::accept);
    }
    public Listener listenIsLiked(String postId, String userId,Consumer<Boolean> onComplete, Consumer<Exception> onFailure){
        final ListenerRegistration listenerRegistration = postsCollection.document(postId).collection(LIKES)
                .document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e !=null){
                    onFailure.accept(e);
                    return;
                }
                assert documentSnapshot != null;
                onComplete.accept(documentSnapshot.exists());
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenTotalLikesCount(String postId, Consumer<Integer> onComplete, Consumer<Exception> onFailure){
        final ListenerRegistration listenerRegistration = postsCollection.document(postId).collection(LIKES).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                onComplete.accept(queryDocumentSnapshots.getDocuments().size());
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

}
