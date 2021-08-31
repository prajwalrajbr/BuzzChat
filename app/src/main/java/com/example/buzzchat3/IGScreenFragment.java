package com.example.buzzchat3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzchat3.Notifications.Token;
import com.example.buzzchat3.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IGScreenFragment extends Fragment {
    private PostAdapter postAdapter;
    private RecyclerView recyclerView;
    private List<Post> postsList;
    private List<String> friendsList;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    FirebaseUser fUser;
    DatabaseReference reference;
    boolean clicked = true;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){



        View view =  inflater.inflate(R.layout.fragment_igscreen,container,false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postsList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postsList);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_stories);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");

        final FloatingActionButton fabBtn = (FloatingActionButton) view.findViewById(R.id.fabBtn);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),PostActivity.class);
                startActivity(i);
            }
        });


        final Animation rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open_anim);
        final Animation rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close_anim);
        final Animation fromBottom = AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_anim);
        final Animation toBottom = AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_anim);
        final FloatingActionButton fabExpander = (FloatingActionButton) view.findViewById(R.id.fabExpander);
        final FloatingActionButton myPosts = (FloatingActionButton) view.findViewById(R.id.myPosts);
        final FloatingActionButton savedPosts = (FloatingActionButton) view.findViewById(R.id.savedPosts);
        final FloatingActionButton igNotification = (FloatingActionButton) view.findViewById(R.id.igNotification);

        RelativeLayout homeActivity = (RelativeLayout) view.findViewById(R.id.homeActivity);
        homeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked){
                    myPosts.setVisibility(View.GONE);
                    savedPosts.setVisibility(View.GONE);
                    igNotification.setVisibility(View.GONE);
                    myPosts.startAnimation(toBottom);
                    savedPosts.setAnimation(toBottom);
                    fabExpander.setAnimation(rotateClose);
                    clicked = !clicked;
                }
            }
        });

        fabExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clicked){
                    myPosts.setVisibility(View.VISIBLE);
                    savedPosts.setVisibility(View.VISIBLE);
                    igNotification.setVisibility(View.VISIBLE);
                    myPosts.startAnimation(fromBottom);
                    savedPosts.setAnimation(fromBottom);
                    igNotification.setAnimation(fromBottom);
                    fabExpander.setAnimation(rotateOpen);
                }else{
                    myPosts.setVisibility(View.GONE);
                    savedPosts.setVisibility(View.GONE);
                    igNotification.setVisibility(View.GONE);
                    myPosts.startAnimation(toBottom);
                    savedPosts.setAnimation(toBottom);
                    igNotification.setAnimation(toBottom);
                    fabExpander.setAnimation(rotateClose);
                }
                clicked = !clicked;
            }
        });

        myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),MyPostsActivity.class);
                startActivity(i);
            }
        });

        savedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),SavedPostsActivity.class);
                startActivity(i);
            }
        });

        igNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),IGNotificationsActivity.class);
                startActivity(i);
            }
        });
        checkFriends();
        return view;
    }

    private void checkFriends(){
        friendsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Friends")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    friendsList.add(dataSnapshot.getKey());
                }
                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postsList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for(String id: friendsList){
                        if(post.getPublisher().equals(id)){
                            postsList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long timeCurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for(String id: friendsList){
                    int countStory = 0;
                    Story story = null;
                    for(DataSnapshot dataSnapshot: snapshot.child(id).getChildren()){
                        story = dataSnapshot.getValue(Story.class);
                        if(timeCurrent>story.getTimestart()&&timeCurrent<story.getTimeend()){
                            countStory++;
                        }
                    }
                    if(countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}