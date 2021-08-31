package com.example.buzzchat3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class IGNotificationsActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private IGNotificationsAdapter igNotificationsAdapter;
    private List<IGNotification> igNotificationsList;
    private Date time[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_g_notifications);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        igNotificationsList = new ArrayList<>();
        igNotificationsAdapter = new IGNotificationsAdapter(getApplicationContext(), igNotificationsList);
        recyclerView.setAdapter(igNotificationsAdapter);
        
        readIGNotifications();
        
    }

    private void readIGNotifications() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                igNotificationsList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    IGNotification igNotification = dataSnapshot.getValue(IGNotification.class);
                    igNotificationsList.add(igNotification);
                }
//                time = new Date[igNotificationsList1.size()];
//
//                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                for(int i=0;i<igNotificationsList1.size();i++){
//                    try {
//                        time[i] = dateFormat.parse(igNotificationsList1.get(i).getTime());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                Arrays.sort(time, Collections.reverseOrder());
//                for(Date d: time)
//                    Log.v("sdf",dateFormat.format(d));
//
//                igNotificationsList.clear();
//                for(int i=igNotificationsList1.size()-1;i>=0;i--){
//                    for(int j=0;j<igNotificationsList1.size();j++){
//                        if(igNotificationsList1.get(j).getTime().equals(dateFormat.format(time[i]))){
//                            Log.v("sdf",Integer.toString(j));
//                            igNotificationsList.add(igNotificationsList1.get(i));
//                            break;
//                        }
//                    }
//                }
//
//                for(int i=0;i<igNotificationsList.size();i++){
//                    Log.v("fgh",igNotificationsList.get(i).getTime());
//                }

                igNotificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}