package com.example.test.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.test.R;
import com.example.test.adapter.NotificationAdapter;


public class NotificationFragment extends Fragment {

    private NotificationAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv_post = view.findViewById(R.id.rv_notifications_list);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv_post.setHasFixedSize(false);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_post.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new NotificationAdapter(getContext(), user -> {
            Log.d("PostFragment","travel to user profile :"+user.get_id());
//            final NavDirections action = PostFragmentDirections.actionPostFragmentToOtherUserProfileFragment(user.get_id());
//            Navigation.findNavController(requireView()).navigate(action);
        }, post -> {
            Log.d("PostFragment","travel to comment of post :"+post.get_id());
            // safeArgs
//            final NavDirections action = PostFragmentDirections.actionPostFragmentToCommentFragment(post);
//            Navigation.findNavController(requireView()).navigate(action);
        });
        rv_post.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
