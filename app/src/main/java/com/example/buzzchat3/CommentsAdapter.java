package com.example.buzzchat3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{
    private Context mContext;
    private List<Comment> mComment;
    private String postid;
    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mContext, List<Comment> mComment,String postid) {
        this.mContext = mContext;
        this.mComment = mComment;
        this.postid = postid;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item_design, parent, false);
        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComment.get(position);
        holder.comment.setText(comment.getComment());
        getUserInfo(holder.image_profile, holder.username, comment.getPublisher());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent ProfileIntent = new Intent(mContext, MyProfileActivity.class);
                    mContext.startActivity(ProfileIntent);
                }else{
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher());
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Friends friends = snapshot.getValue(Friends.class);
                            Intent intent = new Intent(mContext,  UserProfileActivity.class);
                            intent.putExtra("visit_user_id",comment.getPublisher());
                            intent.putExtra("profile_image",friends.getImage());
                            intent.putExtra("profile_name",friends.getName());
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent ProfileIntent = new Intent(mContext, MyProfileActivity.class);
                    mContext.startActivity(ProfileIntent);
                }else{
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher());
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Friends friends = snapshot.getValue(Friends.class);
                            Intent intent = new Intent(mContext,  UserProfileActivity.class);
                            intent.putExtra("visit_user_id",comment.getPublisher());
                            intent.putExtra("profile_image",friends.getImage());
                            intent.putExtra("profile_name",friends.getName());
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(comment.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    Intent ProfileIntent = new Intent(mContext, MyProfileActivity.class);
                    mContext.startActivity(ProfileIntent);
                }else{
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getPublisher());
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Friends friends = snapshot.getValue(Friends.class);
                            Intent intent = new Intent(mContext,  UserProfileActivity.class);
                            intent.putExtra("visit_user_id",comment.getPublisher());
                            intent.putExtra("profile_image",friends.getImage());
                            intent.putExtra("profile_name",friends.getName());
                            mContext.startActivity(intent);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(comment.getPublisher().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog = new AlertDialog.Builder(mContext, R.style.AlertDialog).create();
                    alertDialog.setTitle("Do you want to delete?");
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid)./*child(comment.getCommentid()).*/addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot singleSnapshot : snapshot.getChildren()){
                                                Comment c= singleSnapshot.getValue(Comment.class);
                                                if(c.getCommentid().equals(comment.getCommentid())){
                                                    FirebaseDatabase.getInstance().getReference("Notifications").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot snapshot1: snapshot.getChildren()){
                                                                for(DataSnapshot dataSnapshot: snapshot1.getChildren()){
                                                                    if(c.getCommentNotificationid().equals(dataSnapshot.getKey())){
                                                                        FirebaseDatabase.getInstance().getReference("Notifications").child(snapshot1.getKey()).child(dataSnapshot.getKey()).removeValue();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("error",error.toException().toString());
                                        }
                                    });

                                    FirebaseDatabase.getInstance().getReference("Comments")
                                            .child(postid).child(comment.getCommentid())
                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(mContext,"Deleted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialogInterface.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }



    private void getUserInfo(ImageView imageView, TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Friends friends = snapshot.getValue(Friends.class);
                if(mContext!=null)
                Glide.with(mContext.getApplicationContext()).load(friends.getImage()).into(imageView);
                username.setText(friends.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
