package com.example.buzzchat3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.buzzchat3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class VideoCallActivity extends AppCompatActivity {

    private TextView calling, userName;
    private ImageView profileImg, receiveBtn, rejectBtn;
    private String receiverUID="", receiverProfileImg="", receiverUserName="";
    private String senderUID="", senderProfileImg="", senderUserName="";
    private String call="";
    private String callingID="", ringingID="";
    private DatabaseReference userRef;
    private int iz = 0;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        calling = (TextView)findViewById(R.id.callingLabel);
        userName = (TextView)findViewById(R.id.callerID);
        profileImg = (ImageView)findViewById(R.id.videoCallImage);
        receiveBtn = (ImageView)findViewById(R.id.receiveCall);
        rejectBtn = (ImageView)findViewById(R.id.rejectCall);

        receiverUID = getIntent().getExtras().get("visitUID").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mediaPlayer = MediaPlayer.create(this, R.raw.ringtone);

        senderUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getAndSetReceiverProfileInfo();

        rejectBtn.setVisibility(View.VISIBLE);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                call = "false";
                cancelCall();
            }
        });

        receiveBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                mediaPlayer.stop();
                final HashMap<String, Object> callingPickupMap = new HashMap<>();
                callingPickupMap.put("picked", "picked");
                cancelCall();
                userRef.child(senderUID).updateChildren(callingPickupMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()){
                            Intent i = new Intent(VideoCallActivity.this, MainActivity.class);
                            Log.v("TAc","startedc");
                            startActivity(i);
                            finish();
                        }
                    }
                });
            }
        });


    }

    private void cancelCall() {

        //From sender side
        userRef.child(senderUID).child("calling").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("calling")){
                    callingID = snapshot.child("calling").getValue().toString();
                    userRef.child(callingID).child("ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                userRef.child(senderUID).child("calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //From receiver side
        userRef.child(senderUID).child("ringing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChild("ringing")){
                    ringingID = snapshot.child("ringing").getValue().toString();
                    userRef.child(ringingID).child("calling").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                userRef.child(senderUID).child("ringing").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mediaPlayer.start();

        final boolean[] callPlaced = new boolean[1];
        callPlaced[0]=false;
        userRef.child(senderUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){

                    if(dataSnapshot.getKey().equals("ringing")||dataSnapshot.getKey().equals("calling")){
                        callPlaced[0] = true;
                        break;
                    }
                }
                Log.v("TAcpickedauto","callplaced"+Boolean.toString(callPlaced[0]));

                if(!callPlaced[0]){

                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    HashMap<String, Object> hashMap2 = new HashMap<>();
                    hashMap1.put("ringing",senderUID);
                    userRef.child(receiverUID).child("ringing").setValue(hashMap1);
                    hashMap2.put("calling",receiverUID);
                    userRef.child(senderUID).child("calling").setValue(hashMap2);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(senderUID).hasChild("ringing")){
                    receiveBtn.setVisibility(View.VISIBLE);
                }
                if (snapshot.child(receiverUID).hasChild("picked") ){

                    if (iz ==0){
                        iz=1;
                        mediaPlayer.stop();
                        userRef.child(receiverUID).child("picked").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Intent i = new Intent(VideoCallActivity.this, MainActivity.class);
                                    Log.v("TAcpickedauto","startedc");
                                    receiveBtn.setVisibility(View.VISIBLE);
                                    startActivity(i);
                                    iz=0;
                                    finish();
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getAndSetReceiverProfileInfo() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(receiverUID).exists()){
                    receiverProfileImg = snapshot.child(receiverUID).child("image").getValue().toString();
                    receiverUserName = snapshot.child(receiverUID).child("name").getValue().toString();

                    userName.setText(receiverUserName.toUpperCase());
                    Picasso.get().load(receiverProfileImg).placeholder(R.drawable.default_profile_pic).into(profileImg);
                }

                if(snapshot.child(senderUID).exists()){
                    senderProfileImg = snapshot.child(receiverUID).child("image").getValue().toString();
                    senderUserName = snapshot.child(receiverUID).child("name").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}