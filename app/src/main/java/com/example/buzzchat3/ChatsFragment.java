package com.example.buzzchat3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buzzchat3.Notifications.Token;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChatsFragment extends Fragment {
    private static final String TAG = "ChatsFragment";

    private FriendsAdapter friendsAdapter;
    private RecyclerView recyclerView;
    private List<Friends> mFriends;


    FirebaseUser fUser;
    DatabaseReference reference;
    private List<String> friendsList;
    boolean clicked = true;

    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){



        View view =  inflater.inflate(R.layout.fragment_chats,container,false);
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.progress_circular);

        friendsList = new ArrayList<>();

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if(chat.getSender().equals(fUser.getUid())){
                        friendsList.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(fUser.getUid())){
                        friendsList.add(chat.getSender());
                    }
                }
                Collections.reverse(friendsList);
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final Animation rotateOpen = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_open_anim);
        final Animation rotateClose = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_close_anim);
        final Animation fromBottom = AnimationUtils.loadAnimation(getContext(),R.anim.from_bottom_anim);
        final Animation toBottom = AnimationUtils.loadAnimation(getContext(),R.anim.to_bottom_anim);
        final FloatingActionButton fabExpander = (FloatingActionButton) view.findViewById(R.id.fabExpander);
        final FloatingActionButton fabFriends = (FloatingActionButton) view.findViewById(R.id.fabFriends);
        final FloatingActionButton fabNotifications = (FloatingActionButton) view.findViewById(R.id.fabNotifications);
        final FloatingActionButton fabAddFriends = (FloatingActionButton) view.findViewById(R.id.fabAddFriends);

        RelativeLayout homeActivity = (RelativeLayout) view.findViewById(R.id.homeActivity);
        homeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!clicked){
                    fabFriends.setVisibility(View.GONE);
                    fabNotifications.setVisibility(View.GONE);
                    fabAddFriends.setVisibility(View.GONE);
                    fabFriends.startAnimation(toBottom);
                    fabNotifications.setAnimation(toBottom);
                    fabAddFriends.setAnimation(toBottom);
                    fabExpander.setAnimation(rotateClose);
                    clicked = !clicked;
                }
            }
        });

        fabExpander.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clicked){
                    fabFriends.setVisibility(View.VISIBLE);
                    fabNotifications.setVisibility(View.VISIBLE);
                    fabAddFriends.setVisibility(View.VISIBLE);
                    fabFriends.startAnimation(fromBottom);
                    fabNotifications.setAnimation(fromBottom);
                    fabAddFriends.setAnimation(fromBottom);
                    fabExpander.setAnimation(rotateOpen);
                }else{
                    fabFriends.setVisibility(View.GONE);
                    fabNotifications.setVisibility(View.GONE);
                    fabAddFriends.setVisibility(View.GONE);
                    fabFriends.startAnimation(toBottom);
                    fabNotifications.setAnimation(toBottom);
                    fabAddFriends.setAnimation(toBottom);
                    fabExpander.setAnimation(rotateClose);
                }
                clicked = !clicked;
            }
        });

        fabFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),FriendsListActivity.class);
                startActivity(i);
            }
        });

        fabAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),FindPeopleActivity.class);
                startActivity(i);
            }
        });

        fabNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(),NotificationsActivity.class);
                startActivity(i);
            }
        });
        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(fUser.getUid()).setValue(token1);
    }

    private void readChats(){
        mFriends = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFriends.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Friends friend = dataSnapshot.getValue(Friends.class);
                    assert friend!=null;

                    for(String id:friendsList){
                        Log.v("gsd",id);
                        assert id!=null;
                        if(friend.getUid()!=null)
                        if(friend.getUid().equals(id)){
                            if(mFriends.size()!=0){
                                try {
                                for(Friends friends1: mFriends){
                                    if(!friend.getUid().equals(friends1.getUid())){
                                        mFriends.add(friend);
                                    }
                                }}
                                catch (Exception e){

                                }
                            }else {
                                mFriends.add(friend);
                            }
                        }
                    }
                }
                ArrayList<String> newList = new ArrayList<String>();
                ArrayList<Friends> newmList = new ArrayList<Friends>();

                for (String element : friendsList) {
                    if (!newList.contains(element)) {

                        newList.add(element);
                    }
                }

                for (String s: newList){
                    for(Friends f: mFriends){
                        if(s.equals(f.getUid())){
                            newmList.add(f);
                            break;
                        }
                    }
                }
                progressBar.setVisibility(View.GONE);
                friendsAdapter = new FriendsAdapter(getContext(), newmList, true);
                recyclerView.setAdapter(friendsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
