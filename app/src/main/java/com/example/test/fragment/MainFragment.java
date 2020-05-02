package com.example.test.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.adapter.PostAdapter;
import com.example.test.contollers.database.Database;
import com.example.test.models.User;
import com.example.test.models.listener.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainFragment extends Fragment {


    private PostAdapter mAdapter;

    private Map<String,User> userMap; // user id to user
    private Map<String, UserListenerCount> stringListenerMap; // user id to user listener

    private Listener postListener=null;


    private static class UserListenerCount {
        @NonNull
        public String userid;
        @NonNull
        Listener userListener;
        int count;

        UserListenerCount(@NonNull String userid, @NonNull Listener userListener) {
            this.userid = userid;
            this.userListener = userListener;
            this.count = 0;
        }

        void addListener(){
            this.count++;
        }
        void removerListener(){
            this.count--;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserListenerCount that = (UserListenerCount) o;
            return userid.equals(that.userid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userid);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d("MainFragment","onCreateView");
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("MainFragment","onViewCreated");

//        btn_logout = (Button) view.findViewById(R.id.btn_logout);
        RecyclerView recyclerView = view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new PostAdapter(getContext(), user -> {
            Log.d("MainFragment","travel to user profile :"+user.get_id());
            final MainFragmentDirections.ActionMainFragmentToOtherUserProfileFragment action = MainFragmentDirections.actionMainFragmentToOtherUserProfileFragment(user.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        }, post -> {
            Log.d("MainFragment","travel to comment of post :"+post.get_id());
            final MainFragmentDirections.ActionMainFragmentToCommentFragment action = MainFragmentDirections.actionMainFragmentToCommentFragment(post.get_id());
            Navigation.findNavController(requireView()).navigate(action);
        });
        recyclerView.setAdapter(mAdapter);

        userMap =new HashMap<>();
        stringListenerMap = new HashMap<>();

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("MainFragment","onActivityCreated");

        this.postListener = Database.Post.listenPostsChanges(post -> {
            // added post
            Log.d("MainFragment","on post added: "+post.get_id());
            final String postUserId = post.get_userId();
            if (!userMap.containsKey(postUserId)) {
                final Listener userListener = Database.User.listenUser(postUserId, user -> {
                    userMap.put(postUserId,user); //get connect between user and post
                    mAdapter.updateData(post,user); //display information
                    final UserListenerCount userListenerCount = stringListenerMap.get(postUserId); // sync problem
                    assert userListenerCount != null;
                    userListenerCount.addListener();
                    Log.d("MainFragment","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
                }, e -> {
                    Log.e("MainFragment","Error: "+e.getMessage());
                    e.printStackTrace();
                });
                stringListenerMap.put(postUserId,new UserListenerCount(postUserId, userListener));
            }else{
                mAdapter.updateData(post,userMap.get(postUserId)); //display information
                final UserListenerCount userListenerCount = stringListenerMap.get(postUserId);
                assert userListenerCount != null;
                userListenerCount.addListener();
                Log.d("MainFragment","user listener added for "+postUserId+"\t count: "+ userListenerCount.count);
            }
        }, post -> {
            // modified post
            Log.d("MainFragment","on post modified: "+post.get_id());
            mAdapter.updateData(post, userMap.get(post.get_userId()));
        }, post -> { // remove connection from user when deleted post
            // remove post
            Log.d("MainFragment","on post removed: "+post.get_id());
            mAdapter.removeData(post); //delete post
            UserListenerCount userListenerCount = stringListenerMap.get(post.get_userId());
            assert userListenerCount != null;
            userListenerCount.removerListener();//this post not anymore liston to user
            Log.d("MainFragment","user listener removed for "+post.get_id()+"\t count: "+ userListenerCount.count);
            if (userListenerCount.count <= 0){ // if no one left listen to this user
                userListenerCount.userListener.remove(); // remove connection
                stringListenerMap.remove(post.get_userId()); //
                userMap.remove(post.get_userId()); //
                Log.d("MainFragment","user listener removed for "+post.get_id()+"\t removed");
            }


        }, e -> {
            // on error
            Log.e("MainFragment","Error: "+e.getMessage());
            e.printStackTrace();
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("MainFragment","onDestroyView");

        if (this.postListener!=null){
            this.postListener.remove();
        }
        userMap.clear();
        stringListenerMap.forEach((userId, userListenerCount) -> {
            Log.d("MainFragment","clear connection for "+userId+" had "+userListenerCount.count+" connection");
            userListenerCount.removerListener();
        });
        stringListenerMap.clear();

    }


}