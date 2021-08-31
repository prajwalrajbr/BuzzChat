package com.example.buzzchat3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView FriendsList;
    private DatabaseReference friendsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUid, userName="", profileImage="", calledBy="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);


        try{

            getSupportActionBar().hide();
        }catch (Exception e){
        }getActionBar().setTitle("Hello world App");
        getSupportActionBar().setTitle("Hello world App");

        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);

        FriendsList = (RecyclerView) findViewById(R.id.friendsList);
        FriendsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        //checkForReceivingCall();
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(friendsRef.child(currentUid), Friends.class).build();

        FirebaseRecyclerAdapter<Friends, FriendsListActivity.friendsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Friends, FriendsListActivity.friendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsListActivity.friendsViewHolder holder, int position, @NonNull Friends model) {

                final String listUserId = getRef(position).getKey();
                userRef.child(listUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            userName = snapshot.child("name").getValue().toString();
                            profileImage = snapshot.child("image").getValue().toString();

                            holder.userNameText.setText(userName.toUpperCase());
                            Picasso.get().load(profileImage).into(holder.FriendImage);

                        }

                        holder.videoCallFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent VideoCallIntent = new Intent(ChatsActivity.this, VideoCallActivity.class);
                                VideoCallIntent.putExtra("visitUID",listUserId);

                                startActivity(VideoCallIntent);
                            }
                        });
                        holder.messageFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent MessageIntent = new Intent(ChatsActivity.this, MessageActivity.class);
                                MessageIntent.putExtra("visitUID",listUserId);

                                startActivity(MessageIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public FriendsListActivity.friendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_design, parent, false);
                FriendsListActivity.friendsViewHolder viewHolder = new FriendsListActivity.friendsViewHolder(view);
                return viewHolder;
            }
        };
        FriendsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();


    }

    private void checkForReceivingCall() {

        userRef.child(currentUid).child("ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("ringing")){
                    calledBy = snapshot.child("ringing").getValue().toString();
                    Intent VideoCallIntent = new Intent(ChatsActivity.this, VideoCallActivity.class);
                    VideoCallIntent.putExtra("visitUID",calledBy);
                    Log.v("FrndAc","st");
                    startActivity(VideoCallIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static class friendsViewHolder extends RecyclerView.ViewHolder{

        TextView userNameText;
        ImageButton messageFriend, audioCallFriend, videoCallFriend;
        ImageView FriendImage;
        RelativeLayout cardViewNotification;

        public friendsViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameText = (TextView) itemView.findViewById(R.id.FriendUsername);
            messageFriend = (ImageButton) itemView.findViewById(R.id.messageFriend);
            audioCallFriend = (ImageButton) itemView.findViewById(R.id.audioCallFriend);
            videoCallFriend = (ImageButton) itemView.findViewById(R.id.videoCallFriend);
            FriendImage = (ImageView) itemView.findViewById(R.id.FriendImage);
            cardViewNotification = (RelativeLayout) itemView.findViewById(R.id.cardViewFriendsList);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
    }




}