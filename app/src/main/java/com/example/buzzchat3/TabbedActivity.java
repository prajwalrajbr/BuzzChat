package com.example.buzzchat3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TabbedActivity extends AppCompatActivity {


    private static final String TAG = "TabbedActivity";
    private SectionsPageAdapter sectionPageAdapter;
    private ViewPager viewPager;
    private FirebaseAuth mAuth;
    private String currentUid;

    private DatabaseReference userRef;
    private String calledBy = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sectionPageAdapter = new SectionsPageAdapter((getSupportFragmentManager()));
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);


        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUid = mAuth.getCurrentUser().getUid();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        checkForReceivingCall();

    }

    @Override
    protected void onStart() {
        super.onStart();

        ValidateUser();
    }

    private void ValidateUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        reference.child("Users").child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("image").exists()) {
                    Intent ProfileIntent = new Intent(TabbedActivity.this, MyProfileActivity.class);
                    startActivity(ProfileIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        userRef = FirebaseDatabase.getInstance().getReference("Chats");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
                int unread = 0;
                Set<String> hash_Set = new HashSet<String>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chats = dataSnapshot.getValue(Chats.class);
                    if(chats.getReceiver().equals(mAuth.getUid())&&!chats.isIsseen()){
                        hash_Set.add(chats.getSender());
                    }
                }
                unread = hash_Set.size();
                if(unread==0){
                    adapter.addFragment(new ChatsFragment(), "Chats");
                }else {
                    adapter.addFragment(new ChatsFragment(), "("+unread+") Chats");
                }

                adapter.addFragment(new IGScreenFragment(), "Feed");
                viewPager.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent logOutIntent = new Intent(this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logOutIntent);
                finish();
                return true;
            case R.id.Profile:
                Intent profile = new Intent(this, MyProfileActivity.class);
                startActivity(profile);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void checkForReceivingCall() {

        userRef.child(currentUid).child("ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("ringing")) {
                    calledBy = snapshot.child("ringing").getValue().toString();
                    Intent VideoCallIntent = new Intent(TabbedActivity.this, VideoCallActivity.class);
                    VideoCallIntent.putExtra("visitUID", calledBy);
                    Log.v("TAc","st");

                    startActivity(VideoCallIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status){
        if(mAuth.getUid()!=null)
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);

        userRef.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {

        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}