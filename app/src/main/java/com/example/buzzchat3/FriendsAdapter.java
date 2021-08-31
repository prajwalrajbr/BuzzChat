package com.example.buzzchat3;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.buzzchat3.Friends;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private Context mContext;
    private List<Friends> mFriends;
    private Boolean isChats;
    private String theLastMsg;

    public FriendsAdapter(@NonNull Context context, List<Friends> mFriends, boolean isChats) {
        this.mFriends = mFriends;
        this.mContext = context;
        this.isChats = isChats;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.find_friend_design, parent, false);
        return new FriendsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friends friends = mFriends.get(position);
        holder.userNameText.setText(friends.getName());
        if(friends.getBio().length()>80){
            holder.bioText.setText(friends.getBio().substring(0,80)+"...");
        }else{
            holder.bioText.setText(friends.getBio());
        }

        if(isChats){
            holder.last_msg.setVisibility(View.VISIBLE);
            lastMessage(friends.getUid(), holder.last_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }

        if(isChats){
            if(friends.getStatus().equals("online")){
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
            holder.bioText.setVisibility(View.GONE);
        }else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        if(friends.getImage().equals("")){
            holder.profileImageView.setImageResource(R.drawable.default_profile_pic);
        }else{
            Glide.with(mContext).load(friends.getImage()).into(holder.profileImageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent MessageIntent = new Intent(mContext, MessageActivity.class);
                MessageIntent.putExtra("visitUID",friends.getUid());

                mContext.startActivity(MessageIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userNameText, bioText, last_msg;
        public Button acceptBtn, rejectBtn;
        public ImageView profileImageView;
        public RelativeLayout cardViewNotification;
        private ImageView img_on,img_off;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameText = (TextView) itemView.findViewById(R.id.findFriendUsername);
            bioText = (TextView) itemView.findViewById(R.id.findFriendBio);
            acceptBtn = (Button) itemView.findViewById(R.id.acceptBtn);
            rejectBtn = (Button) itemView.findViewById(R.id.rejectBtn);
            profileImageView = (ImageView) itemView.findViewById(R.id.findFriendImage);
            cardViewNotification = (RelativeLayout) itemView.findViewById(R.id.cardViewNotification);
            img_on = itemView.findViewById(R.id.image_on);
            img_off = itemView.findViewById(R.id.image_off);
            last_msg = itemView.findViewById(R.id.last_msg);

        }
    }

    private void lastMessage(String userID, TextView last_msg){
        theLastMsg = "default";
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if(chats.getReceiver().equals(fUser.getUid()) && chats.getSender().equals(userID) ||
                            chats.getReceiver().equals(userID) && chats.getSender().equals(fUser.getUid())){
                        theLastMsg = chats.getMessage();
                    }
                }
                switch (theLastMsg){
                    case "default":
                        last_msg.setText("No Message");
                        break;
                    default:
                        last_msg.setText(theLastMsg);
                        break;
                }
                theLastMsg="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
