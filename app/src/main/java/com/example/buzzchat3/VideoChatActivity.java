package com.example.buzzchat3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoChatActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {

    private static String API_KEY = "47276134";
    private static String SESSION_ID = "1_MX40NzI3NjEzNH5-MTYyNjAyNzE2NTUwN344R1VLYWpObjU0Q2tScCtwSjRxTEZpRUd-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NzI3NjEzNCZzaWc9NzZhMzg3ODk2NDUwMTNiNDE3Y2QyNTM4ZTdjMDJhN2MwNDJjZTJkMzpzZXNzaW9uX2lkPTFfTVg0ME56STNOakV6Tkg1LU1UWXlOakF5TnpFMk5UVXdOMzQ0UjFWTFlXcE9ialUwUTJ0U2NDdHdTalJ4VEVacFJVZC1mZyZjcmVhdGVfdGltZT0xNjI2MDI4MTg2Jm5vbmNlPTAuNDgxMTYwODA2ODk0ODE0NTUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTYyODYyMDE4MyZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";
    private static final String LOG_TAG = VideoChatActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;
    private ImageView endVideoChatBtn;
    private DatabaseReference userRef;
    private String userId = "";
    private FrameLayout mPublisherViewController;
    private FrameLayout mSubscriberViewController;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_chat);

        Log.d("connected","connected");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        endVideoChatBtn = (ImageView)findViewById(R.id.end_video_chat_btn);
        endVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.child(userId).hasChild("picked")) {
                            userRef.child(userId).child("picked").removeValue();
                        }

                        if (snapshot.child(userId).hasChild("ringing")){
                            userRef.child(userId).child("ringing").removeValue();
                            if (mPublisher!=null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber!=null){
                                mSubscriber.destroy();
                            }
                            finish();
                        }
                        if (snapshot.child(userId).hasChild("calling")){
                            userRef.child(userId).child("calling").removeValue();
                            if (mPublisher!=null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber!=null){
                                mSubscriber.destroy();
                            }
                            finish();
                        }
                        else {
                            if (mPublisher!=null){
                                mPublisher.destroy();
                            }
                            if (mSubscriber!=null){
                                mSubscriber.destroy();
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        requestPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoChatActivity.this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions(){
        String[] perms = {Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

        if (EasyPermissions.hasPermissions(this, perms)){

            mPublisherViewController = (FrameLayout) findViewById(R.id.publisher_container);
            mSubscriberViewController = (FrameLayout) findViewById(R.id.subscriber_container);

            mSession = new Session.Builder(this, API_KEY, SESSION_ID).build();
            mSession.setSessionListener(VideoChatActivity.this);
            mSession.connect(TOKEN);

        }
        else {
            EasyPermissions.requestPermissions(this, "Access Required!!!", RC_VIDEO_APP_PERM);
        }
    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i("LOG_TAG", "Session Created");

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    @Override
    public void onConnected(Session session) {
        Log.i("LOG_TAG", "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoChatActivity.this);
        mPublisherViewController.addView(mPublisher.getView());
        if(mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i("LOG_TAG", "Session Disconnected");

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i("LOG_TAG", "Stream Received");
        if(mSubscriber == null){
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewController.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i("LOG_TAG", "Session Dropped");
        if(mSubscriber != null){
            mSubscriber = null;
            mSubscriberViewController.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.i("LOG_TAG", "Session Error");

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}